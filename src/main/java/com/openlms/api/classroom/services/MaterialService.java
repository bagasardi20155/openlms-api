package com.openlms.api.classroom.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.openlms.api.auth.domains.Role;
import com.openlms.api.auth.domains.User;
import com.openlms.api.auth.repositories.UserRepository;
import com.openlms.api.classroom.domains.ClassEntity;
import com.openlms.api.classroom.domains.Material;
import com.openlms.api.classroom.domains.MaterialProgress;
import com.openlms.api.classroom.domains.MaterialType;
import com.openlms.api.classroom.dtos.requests.CreateMaterialRequest;
import com.openlms.api.classroom.dtos.requests.UpdateMaterialRequest;
import com.openlms.api.classroom.dtos.responses.MaterialResponse;
import com.openlms.api.classroom.dtos.responses.ProgressResponse;
import com.openlms.api.classroom.helpers.JwtHelper;
import com.openlms.api.classroom.helpers.RequireRole;
import com.openlms.api.classroom.repositories.ClassRepository;
import com.openlms.api.classroom.repositories.EnrollmentRepository;
import com.openlms.api.classroom.repositories.MaterialProgressRepository;
import com.openlms.api.classroom.repositories.MaterialRepository;
import com.openlms.api.commons.exceptions.DomainException;
import com.openlms.api.commons.exceptions.ErrorCode;

import jakarta.transaction.Transactional;

@Service
public class MaterialService {
    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;
    private final MaterialProgressRepository materialProgressRepository;

    public MaterialService(
        ClassRepository classRepository,
        EnrollmentRepository enrollmentRepository,
        MaterialRepository materialRepository,
        UserRepository userRepository,
        MaterialProgressRepository materialProgressRepository
    ) {
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
        this.materialProgressRepository = materialProgressRepository;
    }

    private MaterialResponse mapToResponse(Material material) {
        return new MaterialResponse(
            material.getId(),
            material.getTitle(),
            material.getContentType().name(),
            material.getContent(),
            material.getPath(),
            material.getPosition()
        );
    }

    public List<MaterialResponse> listMaterials(UUID classId, Jwt jwt) {
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Class not found"));

        UUID currentUserId = JwtHelper.userId(jwt);
        String currentRole = JwtHelper.role(jwt);

        boolean isAllowedUser = (Role.TEACHER.toString().equals(currentRole) && classEntity.getTeacher().getId().equals(currentUserId)) || 
            (Role.STUDENT.toString().equals(currentRole) && enrollmentRepository.existsByClassIdAndStudentId(classId, currentUserId));

        if (!isAllowedUser) throw new DomainException(ErrorCode.FORBIDDEN, "Forbidden");

        return materialRepository.findByClassIdAndPublishedOrderByPositionAsc(classId, true)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public MaterialResponse create(UUID classId, Jwt jwt, CreateMaterialRequest request) {
        RequireRole.requireRole(jwt, "TEACHER");

        UUID teacherId = UUID.fromString(jwt.getSubject());
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Class not found"));

        if (!classEntity.getTeacher().getId().equals(teacherId)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "Not your class");
        }

        Material material = new Material();
        material.setClassEntity(classEntity);
        material.setTitle(request.title());
        material.setContentType(MaterialType.valueOf(request.contentType().toUpperCase()));
        material.setContent(request.content());
        material.setPath(request.path());
        material.setPosition(request.position() == null ? 0 : request.position());
        if (request.published() != null) material.setPublished(request.published());

        return mapToResponse(materialRepository.save(material));
    }

    @Transactional
    public MaterialResponse update(UUID classId, UUID materialId, Jwt jwt, UpdateMaterialRequest request) {
        RequireRole.requireRole(jwt, "TEACHER");
        UUID teacherId = UUID.fromString(jwt.getSubject());

        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Class not found"));
        if (!classEntity.getTeacher().getId().equals(teacherId)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "Not your class");
        }

        Material material = materialRepository.findById(materialId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Material not found"));
        if (!material.getClassEntity().getId().equals(classId)) throw new DomainException(ErrorCode.BAD_REQUEST, "Material not in class");

        if (request.title() != null) material.setTitle(request.title());
        if (request.content() != null) material.setContent(request.content());
        if (request.path() != null) material.setPath(request.path());
        if (request.position() != null) material.setPosition(request.position());
        if (request.published() != null) material.setPublished(request.published());

        return mapToResponse(materialRepository.save(material));
    }

    @Transactional
    public void markCompleted(UUID classId, UUID materialId, Jwt jwt) {
        RequireRole.requireRole(jwt, "STUDENT");
        UUID studentId = UUID.fromString(jwt.getSubject());

        if (!enrollmentRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "Not enrolled");
        }

        Material material = materialRepository.findById(materialId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Material not found"));
        if (!material.getClassEntity().getId().equals(classId)) throw new DomainException(ErrorCode.BAD_REQUEST, "Material not in class");

        MaterialProgress materialProgress = materialProgressRepository
            .findByMaterialIdAndStudentId(materialId, studentId)
            .orElseGet(() -> {
                ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Class not found"));
                User student = userRepository.findById(studentId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Student not found"));
                
                MaterialProgress progress = new MaterialProgress();
                progress.setClassEntity(classEntity);
                progress.setMaterial(material);
                progress.setStudent(student);
                return progress;
            });

        materialProgress.setCompletedAt(OffsetDateTime.now());
        materialProgressRepository.save(materialProgress);
    }

    public ProgressResponse getProgress(UUID classId, Jwt jwt) {
        RequireRole.requireRole(jwt, "STUDENT");
        UUID studentId = UUID.fromString(jwt.getSubject());

        if (!enrollmentRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "Not enrolled");
        }

        var ProgressProjection = materialProgressRepository.getProgress(classId, studentId);
        return new ProgressResponse(
                ProgressProjection.getTotalMaterials() == null ? 0 : ProgressProjection.getTotalMaterials(),
                ProgressProjection.getCompletedMaterials() == null ? 0 : ProgressProjection.getCompletedMaterials(),
                ProgressProjection.getProgressPercent() == null ? 0 : ProgressProjection.getProgressPercent()
        );
    }
}

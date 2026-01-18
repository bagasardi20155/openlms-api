package com.openlms.api.classroom.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.openlms.api.auth.domains.Role;
import com.openlms.api.auth.repositories.UserRepository;
import com.openlms.api.classroom.domains.ClassEntity;
import com.openlms.api.classroom.domains.Material;
import com.openlms.api.classroom.dtos.responses.MaterialResponse;
import com.openlms.api.classroom.helpers.JwtHelper;
import com.openlms.api.classroom.repositories.ClassRepository;
import com.openlms.api.classroom.repositories.EnrollmentRepository;
import com.openlms.api.classroom.repositories.MaterialRepository;
import com.openlms.api.commons.exceptions.DomainException;
import com.openlms.api.commons.exceptions.ErrorCode;

@Service
public class MaterialService {
    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;

    public MaterialService(
        ClassRepository classRepository,
        EnrollmentRepository enrollmentRepository,
        MaterialRepository materialRepository,
        UserRepository userRepository
    ) {
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
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
}

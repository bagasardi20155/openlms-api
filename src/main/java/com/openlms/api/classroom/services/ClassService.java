package com.openlms.api.classroom.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.openlms.api.auth.domains.Role;
import com.openlms.api.auth.domains.User;
import com.openlms.api.auth.repositories.UserRepository;
import com.openlms.api.classroom.domains.ClassEntity;
import com.openlms.api.classroom.dtos.requests.CreateClassRequest;
import com.openlms.api.classroom.dtos.requests.PublishClassRequest;
import com.openlms.api.classroom.dtos.responses.ClassResponse;
import com.openlms.api.classroom.repositories.ClassRepository;
import com.openlms.api.commons.exceptions.DomainException;
import com.openlms.api.commons.exceptions.ErrorCode;
import com.openlms.api.classroom.helpers.RequireRole;
import com.openlms.api.classroom.helpers.JwtHelper;

import jakarta.transaction.Transactional;

@Service
public class ClassService {
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    public ClassService(ClassRepository classRepository, UserRepository userRepository) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
    }

    private ClassResponse mapToResponse(ClassEntity classEntity) {
        return new ClassResponse(classEntity.getId(), classEntity.getTeacher().getId(), classEntity.getTitle(), classEntity.getDescription(), classEntity.isPublished());
    }

    @Transactional
    public ClassResponse createClass(Jwt jwt, CreateClassRequest request) {
        RequireRole.requireRole(jwt, "TEACHER");

        UUID teacherId = JwtHelper.userId(jwt);
        User teacher = userRepository.findById(teacherId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Teacher not found"));

        ClassEntity classEntity = new ClassEntity();
        classEntity.setTeacher(teacher);
        classEntity.setTitle(request.title());
        classEntity.setDescription(request.description());
        classEntity.setPublished(false);
        ClassEntity saved = classRepository.save(classEntity);

        return mapToResponse(saved);
    }

    public ClassResponse getClass(UUID classId, Jwt jwt) {
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Class not found"));

        String role = JwtHelper.role(jwt);
        UUID userId = JwtHelper.userId(jwt);

        if (!classEntity.isPublished()) {
            if (!"TEACHER".equals(role) || !classEntity.getTeacher().getId().equals(userId)) {
                throw new DomainException(ErrorCode.FORBIDDEN, "Class not accessible");
            }
        }

        return mapToResponse(classEntity);
    }

    public List<ClassResponse> taughtClasses(Jwt jwt) {
        RequireRole.requireRole(jwt, "TEACHER");
        
        UUID teacherId = JwtHelper.userId(jwt);

        return classRepository.findByTeacherId(teacherId).stream()
            .map(this::mapToResponse)
            .toList();
    }

    public ClassResponse publish(Jwt jwt, PublishClassRequest request) {
        RequireRole.requireRole(jwt, "TEACHER");

        ClassEntity classEntity = classRepository.findById(request.classId()).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Class not found"));
        classEntity.setPublished(!classEntity.isPublished());
        classRepository.save(classEntity);
        return mapToResponse(classEntity);
    }
}

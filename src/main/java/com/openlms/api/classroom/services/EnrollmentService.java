package com.openlms.api.classroom.services;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.openlms.api.auth.domains.User;
import com.openlms.api.auth.repositories.UserRepository;
import com.openlms.api.classroom.domains.ClassEntity;
import com.openlms.api.classroom.domains.Enrollment;
import com.openlms.api.classroom.domains.EnrollmentStatus;
import com.openlms.api.classroom.dtos.responses.EnrollmentResponse;
import com.openlms.api.classroom.helpers.JwtHelper;
import com.openlms.api.classroom.helpers.RequireRole;
import com.openlms.api.classroom.repositories.ClassRepository;
import com.openlms.api.classroom.repositories.EnrollmentRepository;
import com.openlms.api.commons.events.EnrollmentCreatedEvent;
import com.openlms.api.commons.exceptions.DomainException;
import com.openlms.api.commons.exceptions.ErrorCode;

import jakarta.transaction.Transactional;

@Service
public class EnrollmentService {
    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public EnrollmentService(
        ClassRepository classRepository,
        EnrollmentRepository enrollmentRepository,
        UserRepository userRepository,
        KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public EnrollmentResponse enroll(UUID classId, Jwt jwt) {
        RequireRole.requireRole(jwt, "STUDENT");

        UUID studentId = JwtHelper.userId(jwt);

        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Class not found"));
        User student = userRepository.findById(studentId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "Student not found"));

        if (!classEntity.isPublished()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "Class archived");
        }

        if (enrollmentRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new DomainException(ErrorCode.CONFLICT, "Already enrolled");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setClassEntity(classEntity);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        Enrollment saved = enrollmentRepository.save(enrollment);

        var event = new EnrollmentCreatedEvent(
            enrollment.getId(),
            classId,
            studentId,
            student.getEmail(),
            classEntity.getTitle(),
            OffsetDateTime.now()
        );

        kafkaTemplate.send("openlms.enrollment.created", studentId.toString(), event);

        return new EnrollmentResponse(saved.getId(), classId, studentId, saved.getStatus().name());
    }
}

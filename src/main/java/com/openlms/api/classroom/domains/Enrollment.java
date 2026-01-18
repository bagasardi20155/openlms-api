package com.openlms.api.classroom.domains;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.openlms.api.auth.domains.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "student_id"}))
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @Column(name = "enrolled_at", nullable = false)
    private OffsetDateTime enrolledAt;

    @PrePersist
    void prePersist() {
        enrolledAt = OffsetDateTime.now();
        if (status == null) status = EnrollmentStatus.ACTIVE;
    }

    //relateion
    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonBackReference
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonBackReference
    private User student;
}

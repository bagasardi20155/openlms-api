package com.openlms.api.classroom.domains;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.openlms.api.auth.domains.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(name = "material_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"material_id", "student_id"}))
public class MaterialProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    // relation
    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonBackReference
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "material_id")
    @JsonBackReference
    private Material material;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonBackReference
    private User student;
}

package com.openlms.api.classroom.domains;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private MaterialType contentType;

    @Column
    private String content;

    @Column
    private String path;

    @Column(nullable = false)
    private int position;

    @Column(name = "is_published", nullable = false)
    private boolean published;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
        published = true;
    }

    @PreUpdate
    void preUpdate() { 
        updatedAt = OffsetDateTime.now(); 
    }

    // relation
    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonBackReference
    private ClassEntity classEntity;
}

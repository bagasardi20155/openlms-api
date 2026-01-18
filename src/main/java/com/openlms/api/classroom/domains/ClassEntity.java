package com.openlms.api.classroom.domains;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openlms.api.auth.domains.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "classes")
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

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
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // relation
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonBackReference
    private User teacher;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Material> materials;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MaterialProgress> materialProgresses;

}

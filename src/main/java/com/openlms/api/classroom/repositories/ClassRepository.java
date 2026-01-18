package com.openlms.api.classroom.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openlms.api.classroom.domains.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, UUID> {
    List<ClassEntity> findByTeacherId(UUID teacherId);
    boolean existsByIdAndTeacherId(UUID id, UUID teacherId);
}

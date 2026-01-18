package com.openlms.api.classroom.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openlms.api.classroom.domains.MaterialProgress;

public interface MaterialProgressRepository extends JpaRepository<MaterialProgress, UUID> {
    Optional<MaterialProgress> findByMaterialIdAndStudentId(UUID materialId, UUID studentId);

    @Query(value = """
        SELECT
          CAST(COUNT(*) AS float) AS total_materials,
          CAST(SUM(CASE WHEN mp.completed_at IS NOT NULL THEN 1 ELSE 0 END) AS float) AS completed_materials,
          CASE
            WHEN COUNT(*) = 0 THEN 0
            ELSE ROUND((SUM(CASE WHEN mp.completed_at IS NOT NULL THEN 1 ELSE 0 END)::numeric / COUNT(*)::numeric) * 100, 2)
          END AS progress_percent
        FROM materials m
        LEFT JOIN material_progress mp
          ON mp.material_id = m.id
         AND mp.student_id = :studentId
        WHERE m.class_id = :classId
          AND m.is_published = true
        """, nativeQuery = true)
    ProgressProjection getProgress(UUID classId, UUID studentId);

    interface ProgressProjection {
        Double getTotalMaterials();
        Double getCompletedMaterials();
        Double getProgressPercent();
    }
}

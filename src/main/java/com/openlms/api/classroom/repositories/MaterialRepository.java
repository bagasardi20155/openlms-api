package com.openlms.api.classroom.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openlms.api.classroom.domains.Material;

public interface MaterialRepository extends JpaRepository<Material, UUID> {
    @Query(value = 
        """
            select *
            from materials
            where class_id = :classId
            and is_published = :published
            order by position asc            
        """
    , nativeQuery = true)
    List<Material> findByClassIdAndPublishedOrderByPositionAsc(UUID classId, boolean published);
    
    @Query(value = 
        """
            SELECT COUNT(*)     
            FROM materials
            WHERE class_id = :classid
            and is_published = :published
        """
    , nativeQuery = true)
    long countByClassIdAndPublished(UUID classId, boolean published);
}

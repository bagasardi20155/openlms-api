package com.openlms.api.classroom.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openlms.api.classroom.domains.Enrollment;
import com.openlms.api.classroom.domains.EnrollmentStatus;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    @Query(value =
        """
            Select
                case
                    when count(*) > 0 then true
                    else false
                end
            from enrollments
            where class_id = :classId
            and student_id = :studentId   
        """
        , nativeQuery = true)
    boolean existsByClassIdAndStudentId(@Param("classId") UUID classId, @Param("studentId") UUID studentId);

    @Query(value = 
        """
            select *
            from enrollments
            where class_id = :classId
            and student_id = :studentId        
        """
    , nativeQuery = true)
    Optional<Enrollment> findByClassIdAndStudentId(UUID classId, UUID studentId);
    
    List<Enrollment> findByStudentIdAndStatus(UUID studentId, EnrollmentStatus status);
}

package com.schoolmanagment.coreservice.classsection.repository;

import com.schoolmanagment.coreservice.classsection.entity.ClassSectionHomeroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassSectionHomeroomRepository extends JpaRepository<ClassSectionHomeroom, UUID> {

    Optional<ClassSectionHomeroom> findByClassSectionIdAndActiveTrue(UUID classSectionId);

    List<ClassSectionHomeroom> findByClassSectionIdOrderByCreatedAtDesc(UUID classSectionId);

    List<ClassSectionHomeroom> findByTeacherIdAndActiveTrue(UUID teacherId);

    @Query("""
            SELECT h.classSection.id
            FROM ClassSectionHomeroom h
            WHERE h.teacher.id = :teacherId
              AND h.active = true
              AND h.classSection.active = true
            """)
    List<UUID> findActiveClassSectionIdsByTeacherId(@Param("teacherId") UUID teacherId);
}

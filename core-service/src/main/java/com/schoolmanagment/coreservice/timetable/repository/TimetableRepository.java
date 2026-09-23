package com.schoolmanagment.coreservice.timetable.repository;

import com.schoolmanagment.coreservice.timetable.entity.Timetable;
import com.schoolmanagment.coreservice.timetable.enums.Day;
import com.schoolmanagment.coreservice.timetable.enums.Period;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, UUID>, JpaSpecificationExecutor<Timetable> {

    Page<Timetable> findByActiveTrue(Pageable pageable);

    Optional<Timetable> findByIdAndActiveTrue(UUID id);


    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
            FROM Timetable t
            WHERE t.classSection.id = :classSectionId
              AND t.day = :day
              AND t.period = :period
              AND t.active = true
              AND (:excludeId IS NULL OR t.id <> :excludeId)
            """)
    boolean existsConflictingClassSectionSlot(
            @Param("classSectionId") UUID classSectionId,
            @Param("day") Day day,
            @Param("period") Period period,
            @Param("excludeId") UUID excludeId);

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
            FROM Timetable t
            WHERE t.teacherSubjectAssignment.teacher.id = :teacherId
              AND t.day = :day
              AND t.period = :period
              AND t.active = true
              AND (:excludeId IS NULL OR t.id <> :excludeId)
            """)
    boolean existsConflictingTeacherSlot(
            @Param("teacherId") UUID teacherId,
            @Param("day") Day day,
            @Param("period") Period period,
            @Param("excludeId") UUID excludeId);
}

package com.schoolmanagment.coreservice.academicyear.repository;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID>, JpaSpecificationExecutor<AcademicYear> {

    Page<AcademicYear> findByActiveTrue(Pageable pageable);

    Optional<AcademicYear> findByIdAndActiveTrue(UUID id);

    Optional<AcademicYear> findBySchoolIdAndAcYear(UUID schoolId, String acYear);

    Optional<AcademicYear> findBySchoolIdAndActiveTrue(UUID schoolId);

    List<AcademicYear> findAllBySchoolIdAndActiveTrue(UUID schoolId);

    @Query("""
            SELECT y FROM AcademicYear y
            WHERE y.active = true
              AND y.endDate IS NOT NULL
              AND y.endDate < :today
            """)
    List<AcademicYear> findActiveWithEndDateBefore(@Param("today") LocalDate today);
}

package com.schoolmanagment.coreservice.academicyear.repository;

import com.schoolmanagment.coreservice.academicyear.entity.Term;
import com.schoolmanagment.coreservice.academicyear.enums.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TermRepository extends JpaRepository<Term, UUID> {

    Page<Term> findByActiveTrue(Pageable pageable);

    Page<Term> findByAcademicYearIdAndActiveTrue(UUID academicYearId, Pageable pageable);

    Optional<Term> findByIdAndActiveTrue(UUID id);

    Optional<Term> findByAcademicYearIdAndSemester(UUID academicYearId, Semester semester);

    @Query("""
            SELECT t FROM Term t
            WHERE t.academicYear.id = :academicYearId
              AND t.active = true
            ORDER BY t.startDate ASC, t.semester ASC
            """)
    List<Term> findActiveByAcademicYear(@Param("academicYearId") UUID academicYearId);

    Optional<Term> findByAcademicYearIdAndSemesterAndActiveTrue(UUID academicYearId, Semester semester);

    boolean existsByAcademicYearIdAndSemester(UUID academicYearId, Semester semester);

    @Query("""
            SELECT t FROM Term t
            WHERE t.academicYear.id = :academicYearId
              AND t.active = true
              AND :today BETWEEN t.startDate AND t.endDate
            """)
    Optional<Term> findCurrentTerm(
            @Param("academicYearId") UUID academicYearId,
            @Param("today") LocalDate today);
}

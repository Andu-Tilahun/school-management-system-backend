package com.schoolmanagment.coreservice.academicyear.repository;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TermRepository extends JpaRepository<Term, UUID> {

    @Query("""
            SELECT t FROM Term t
            WHERE t.academicYear.id = :academicYearId
              AND t.active = true
            ORDER BY t.sequenceOrder ASC
            """)
    List<Term> findActiveByAcademicYearOrderBySequence(
            @Param("academicYearId") UUID academicYearId);

    @Query("""
            SELECT t FROM Term t
            WHERE t.academicYear.id = :academicYearId
              AND t.sequenceOrder = :nextSequenceOrder
              AND t.active = true
            """)
    Optional<Term> findByAcademicYearAndSequenceOrder(
            @Param("academicYearId") UUID academicYearId,
            @Param("nextSequenceOrder") Integer nextSequenceOrder);

    boolean existsByAcademicYearIdAndNameIgnoreCase(UUID academicYearId, String name);


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


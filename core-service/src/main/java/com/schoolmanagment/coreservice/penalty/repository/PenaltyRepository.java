package com.schoolmanagment.coreservice.penalty.repository;

import com.schoolmanagment.coreservice.penalty.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PenaltyRepository extends JpaRepository<Penalty, UUID> {

    @Query("""
            SELECT p FROM Penalty p
            WHERE p.enrollment.id = :enrollmentId
              AND p.penaltyRule.id = :ruleId
              AND p.active = true
            """)
    Optional<Penalty> findActiveByEnrollmentAndRule(
            @Param("enrollmentId") UUID enrollmentId,
            @Param("ruleId") UUID ruleId);


    List<Penalty> findByEnrollmentIdAndActiveTrue(UUID enrollmentId);


    List<Penalty> findByEnrollmentIdOrderByCreatedAtDesc(UUID enrollmentId);
}

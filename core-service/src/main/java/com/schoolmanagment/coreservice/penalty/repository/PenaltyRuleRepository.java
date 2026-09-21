package com.schoolmanagment.coreservice.penalty.repository;

import com.schoolmanagment.coreservice.penalty.entity.PenaltyRule;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenaltyRuleRepository extends JpaRepository<PenaltyRule, UUID>, JpaSpecificationExecutor<PenaltyRule> {

    Page<PenaltyRule> findByActiveTrue(Pageable pageable);

    Optional<PenaltyRule> findByIdAndActiveTrue(UUID id);

    boolean existsBySchoolIdAndPenaltyTriggerAndOccurrenceNumber(
            UUID schoolId, PenaltyTrigger penaltyTrigger, Integer occurrenceNumber);

    boolean existsBySchoolIdAndPenaltyTriggerAndOccurrenceNumberAndIdNot(
            UUID schoolId, PenaltyTrigger penaltyTrigger, Integer occurrenceNumber, UUID id);

    @Query("""
            SELECT r FROM PenaltyRule r
            WHERE r.penaltyTrigger = :trigger
              AND r.active = true
            ORDER BY r.occurrenceNumber ASC
            """)
    List<PenaltyRule> findActiveByTrigger(@Param("trigger") PenaltyTrigger trigger);
}

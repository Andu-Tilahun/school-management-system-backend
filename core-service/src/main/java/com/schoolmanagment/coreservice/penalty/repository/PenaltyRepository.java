package com.schoolmanagment.coreservice.penalty.repository;

import com.schoolmanagment.coreservice.penalty.entity.Penalty;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, UUID>, JpaSpecificationExecutor<Penalty> {

    Page<Penalty> findByActiveTrue(Pageable pageable);

    Optional<Penalty> findByIdAndActiveTrue(UUID id);

    boolean existsBySchoolIdAndPenaltyTriggerAndOccurrenceNumber(
            UUID schoolId, PenaltyTrigger penaltyTrigger, Integer occurrenceNumber);

    boolean existsBySchoolIdAndPenaltyTriggerAndOccurrenceNumberAndIdNot(
            UUID schoolId, PenaltyTrigger penaltyTrigger, Integer occurrenceNumber, UUID id);
}

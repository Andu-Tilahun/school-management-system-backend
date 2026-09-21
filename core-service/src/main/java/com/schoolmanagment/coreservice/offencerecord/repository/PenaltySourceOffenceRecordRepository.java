package com.schoolmanagment.coreservice.offencerecord.repository;

import com.schoolmanagment.coreservice.offencerecord.entity.PenaltySourceOffenceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PenaltySourceOffenceRecordRepository
        extends JpaRepository<PenaltySourceOffenceRecord, UUID> {

    List<PenaltySourceOffenceRecord> findByPenaltyId(UUID penaltyId);

    List<PenaltySourceOffenceRecord> findByOffenceRecordId(UUID offenceRecordId);
}

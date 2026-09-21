package com.schoolmanagment.coreservice.penalty.repository;

import com.schoolmanagment.coreservice.penalty.entity.PenaltySourceAttendance;
import com.schoolmanagment.coreservice.penalty.entity.PenaltySourceOffenceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PenaltySourceOffenceRecordRepository
        extends JpaRepository<PenaltySourceOffenceRecord, UUID> {

    List<PenaltySourceOffenceRecord> findByPenaltyId(UUID penaltyId);

    List<PenaltySourceOffenceRecord> findByOffenceRecordId(UUID offenceRecordId);
}

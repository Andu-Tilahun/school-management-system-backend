package com.schoolmanagment.coreservice.offencerecord.repository;

import com.schoolmanagment.coreservice.offencerecord.entity.OffenceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OffenceRecordRepository extends JpaRepository<OffenceRecord, UUID>, JpaSpecificationExecutor<OffenceRecord> {

    Page<OffenceRecord> findByActiveTrue(Pageable pageable);

    Optional<OffenceRecord> findByIdAndActiveTrue(UUID id);

    List<OffenceRecord> findByEnrollment_IdAndActiveTrue(UUID enrollmentId);

    List<OffenceRecord> findByAcademicYear_IdAndActiveTrue(UUID academicYearId);
}

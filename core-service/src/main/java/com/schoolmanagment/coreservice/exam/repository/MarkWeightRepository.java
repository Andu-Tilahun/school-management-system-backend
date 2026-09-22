package com.schoolmanagment.coreservice.exam.repository;

import com.schoolmanagment.coreservice.exam.entity.MarkWeight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MarkWeightRepository extends JpaRepository<MarkWeight, UUID> {

    List<MarkWeight> findByTermIdAndSubjectIdAndActiveTrue(UUID termId, UUID subjectId);

}

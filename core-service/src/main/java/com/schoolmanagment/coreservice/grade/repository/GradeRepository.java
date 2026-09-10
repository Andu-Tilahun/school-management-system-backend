package com.schoolmanagment.coreservice.grade.repository;

import com.schoolmanagment.coreservice.grade.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID>, JpaSpecificationExecutor<Grade> {

    Page<Grade> findByActiveTrue(Pageable pageable);

    Optional<Grade> findByIdAndActiveTrue(UUID id);

    boolean existsBySchoolIdAndNameIgnoreCase(UUID schoolId, String name);

    boolean existsBySchoolIdAndNameIgnoreCaseAndIdNot(UUID schoolId, String name, UUID id);
}

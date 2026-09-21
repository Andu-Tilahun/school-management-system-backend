package com.schoolmanagment.coreservice.classsection.repository;

import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassSectionRepository extends JpaRepository<ClassSection, UUID>, JpaSpecificationExecutor<ClassSection> {

    Page<ClassSection> findByActiveTrue(Pageable pageable);

    Optional<ClassSection> findByIdAndActiveTrue(UUID id);

    boolean existsByGrade_Id(UUID gradeId);

    boolean existsByGrade_IdAndIdNot(UUID gradeId, UUID id);

    List<ClassSection> findByGrade_IdAndActiveTrue(UUID gradeId);
}

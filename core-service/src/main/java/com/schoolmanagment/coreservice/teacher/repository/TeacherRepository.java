package com.schoolmanagment.coreservice.teacher.repository;

import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID>, JpaSpecificationExecutor<Teacher> {

    Page<Teacher> findByActiveTrue(Pageable pageable);

    Optional<Teacher> findByIdAndActiveTrue(UUID id);

    Optional<Teacher> findByMobileNumber(String mobileNumber);
}

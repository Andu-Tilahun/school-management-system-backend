package com.schoolmanagment.coreservice.student.repository;

import com.schoolmanagment.coreservice.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {

    Page<Student> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = "emergencyContacts")
    Optional<Student> findByIdAndActiveTrue(UUID id);

    Optional<Student> findByMobileNumber(String mobileNumber);
}

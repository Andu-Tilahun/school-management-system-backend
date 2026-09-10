package com.schoolmanagment.coreservice.classroom.repository;

import com.schoolmanagment.coreservice.classroom.entity.ClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, UUID>, JpaSpecificationExecutor<ClassRoom> {

    Page<ClassRoom> findByActiveTrue(Pageable pageable);

    Optional<ClassRoom> findByIdAndActiveTrue(UUID id);

    boolean existsBySchoolIdAndRoomNumberIgnoreCase(UUID schoolId, String roomNumber);

    boolean existsBySchoolIdAndRoomNumberIgnoreCaseAndIdNot(UUID schoolId, String roomNumber, UUID id);
}

package com.schoolmanagment.coreservice.roomsection.repository;

import com.schoolmanagment.coreservice.roomsection.entity.RoomSection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomSectionRepository extends JpaRepository<RoomSection, UUID>, JpaSpecificationExecutor<RoomSection> {

    Page<RoomSection> findByActiveTrue(Pageable pageable);

    Optional<RoomSection> findByIdAndActiveTrue(UUID id);

    boolean existsBySchoolIdAndClassSection_IdAndRoom_Id(UUID schoolId, UUID classSectionId, UUID roomId);

    boolean existsBySchoolIdAndClassSection_IdAndRoom_IdAndIdNot(UUID schoolId, UUID classSectionId, UUID roomId, UUID id);

    List<RoomSection> findByClassSection_IdAndActiveTrue(UUID classSectionId);

    List<RoomSection> findByRoom_IdAndActiveTrue(UUID roomId);
}

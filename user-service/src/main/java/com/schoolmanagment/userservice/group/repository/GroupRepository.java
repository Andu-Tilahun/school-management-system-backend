package com.schoolmanagment.userservice.group.repository;

import com.schoolmanagment.userservice.group.entity.Group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    boolean existsByName(String name);

    Optional<Group> findByName(String name);

    @EntityGraph(attributePaths = {"policies"})
    @NonNull
    @Override
    Optional<Group> findById(@NonNull UUID id);
}

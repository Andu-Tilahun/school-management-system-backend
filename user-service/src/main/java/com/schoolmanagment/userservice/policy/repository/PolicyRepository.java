package com.schoolmanagment.userservice.policy.repository;

import com.schoolmanagment.userservice.policy.entity.Policy;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, UUID> {
    boolean existsByName(String name);

    Optional<Policy> findByName(String name);

    @EntityGraph(attributePaths = {"permissions"})
    @NonNull
    @Override
    Optional<Policy> findById(@NonNull UUID id);
}

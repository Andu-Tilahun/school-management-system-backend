package com.schoolmanagment.userservice.user.repository;

import com.schoolmanagment.userservice.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"policies", "groups", "groups.policies"})
    Optional<User> findWithPoliciesGraphByUsername(String username);

    @EntityGraph(attributePaths = {"policies", "groups", "groups.policies"})
    @Override
    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}

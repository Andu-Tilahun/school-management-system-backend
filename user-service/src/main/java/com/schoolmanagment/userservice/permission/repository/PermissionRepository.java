package com.schoolmanagment.userservice.permission.repository;

import com.schoolmanagment.userservice.permission.entity.Permission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID>, JpaSpecificationExecutor<Permission> {
    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"resource", "scopes", "policies"})
    @NonNull
    @Override
    Optional<Permission> findById(@NonNull UUID id);
}

package com.schoolmanagment.userservice.scope.repository;

import com.schoolmanagment.userservice.scope.entity.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, UUID> {
    boolean existsByName(String name);
}

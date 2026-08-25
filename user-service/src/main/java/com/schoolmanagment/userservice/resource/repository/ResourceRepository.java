package com.schoolmanagment.userservice.resource.repository;

import com.schoolmanagment.userservice.resource.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    boolean existsByName(String name);
}

package com.schoolmanagment.userservice.menu.repository;

import com.schoolmanagment.userservice.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT DISTINCT m FROM Menu m LEFT JOIN FETCH m.resource ORDER BY m.sortOrder ASC, m.id ASC")
    List<Menu> findAllForNavigationOrdered();
}

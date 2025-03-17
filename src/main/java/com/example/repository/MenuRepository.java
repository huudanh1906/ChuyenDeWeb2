package com.example.repository;

import com.example.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStatus(int status);

    List<Menu> findByPositionAndStatus(String position, int status);

    List<Menu> findByParentId(Long parentId);
}
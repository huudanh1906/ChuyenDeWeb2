package com.example.repository;

import com.example.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByStatus(int status);

    Optional<Brand> findBySlug(String slug);
}
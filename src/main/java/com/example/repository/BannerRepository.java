package com.example.repository;

import com.example.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByStatus(int status);

    List<Banner> findByPositionAndStatus(String position, int status);
}
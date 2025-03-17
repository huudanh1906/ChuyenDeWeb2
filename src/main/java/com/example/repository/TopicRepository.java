package com.example.repository;

import com.example.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByStatus(int status);

    List<Topic> findByParentId(Long parentId);

    Optional<Topic> findBySlug(String slug);
}
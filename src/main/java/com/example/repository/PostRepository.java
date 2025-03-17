package com.example.repository;

import com.example.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(int status);

    List<Post> findByTopicId(Long topicId);

    Optional<Post> findBySlug(String slug);

    List<Post> findByType(String type);
}
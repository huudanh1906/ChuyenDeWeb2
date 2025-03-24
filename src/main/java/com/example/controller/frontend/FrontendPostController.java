package com.example.controller.frontend;

import com.example.entity.Post;
import com.example.entity.Topic;
import com.example.repository.PostRepository;
import com.example.repository.TopicRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/frontend/posts")
@CrossOrigin(origins = "*")
public class FrontendPostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TopicRepository topicRepository;

    /**
     * Get list of posts
     */
    @GetMapping
    public ResponseEntity<?> index(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "6") int size) {

        // Get all posts with status 1 and sort by created_at desc
        List<Post> allActivePosts = postRepository.findAll().stream()
                .filter(post -> post.getStatus() == 1)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());

        // Manual pagination
        int start = (page - 1) * size;
        int end = Math.min(start + size, allActivePosts.size());
        List<Post> paginatedPosts = (start < allActivePosts.size()) ? allActivePosts.subList(start, end)
                : new ArrayList<>();

        // Create response object
        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedPosts);
        response.put("totalElements", allActivePosts.size());
        response.put("totalPages", (int) Math.ceil((double) allActivePosts.size() / size));
        response.put("size", size);
        response.put("number", page - 1);

        return ResponseEntity.ok(response);
    }

    /**
     * Get post detail by slug
     */
    @GetMapping("/{slug}")
    public ResponseEntity<?> postDetail(@PathVariable String slug) {
        // Find post by slug and status
        Optional<Post> postOpt = postRepository.findAll().stream()
                .filter(post -> post.getSlug().equals(slug) && post.getStatus() == 1)
                .findFirst();

        if (postOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Post post = postOpt.get();
        Long topicId = post.getTopicId();

        // Get related posts from same topic (excluding current post)
        List<Post> relatedPosts = postRepository.findAll().stream()
                .filter(p -> p.getTopicId().equals(topicId) && p.getStatus() == 1 && !p.getSlug().equals(slug))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .limit(9)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("list_post", relatedPosts);

        return ResponseEntity.ok(response);
    }

    /**
     * Get posts by topic
     */
    @GetMapping("/topic/{slug}")
    public ResponseEntity<?> postTopic(@PathVariable String slug,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "6") int size) {

        Optional<Topic> topicOpt = topicRepository.findBySlug(slug);
        if (topicOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Topic not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Topic topic = topicOpt.get();
        Long topicId = topic.getId();

        // Get all posts for the topic with status 1 and sort by created_at desc
        List<Post> allTopicPosts = postRepository.findAll().stream()
                .filter(post -> post.getTopicId().equals(topicId) && post.getStatus() == 1)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());

        // Manual pagination
        int start = (page - 1) * size;
        int end = Math.min(start + size, allTopicPosts.size());
        List<Post> paginatedPosts = (start < allTopicPosts.size()) ? allTopicPosts.subList(start, end)
                : new ArrayList<>();

        // Create response object
        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedPosts);
        response.put("totalElements", allTopicPosts.size());
        response.put("totalPages", (int) Math.ceil((double) allTopicPosts.size() / size));
        response.put("size", size);
        response.put("number", page - 1);

        return ResponseEntity.ok(response);
    }

    /**
     * Get page by slug (treated as a special post)
     */
    @GetMapping("/page/{slug}")
    public ResponseEntity<?> pageTopic(@PathVariable String slug) {
        // Find post by slug and status
        Optional<Post> postOpt = postRepository.findAll().stream()
                .filter(post -> post.getSlug().equals(slug) && post.getStatus() == 1)
                .findFirst();

        if (postOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Page not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(postOpt.get());
    }
}
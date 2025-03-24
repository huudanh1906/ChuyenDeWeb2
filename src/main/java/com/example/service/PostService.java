package com.example.service;

import com.example.entity.Post;
import com.example.entity.Topic;
import com.example.repository.PostRepository;
import com.example.repository.TopicRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all active posts
     */
    public List<Post> getAllActivePosts() {
        return postRepository.findAll().stream()
                .filter(post -> post.getStatus() == 1)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all trashed posts
     */
    public List<Post> getAllTrashedPosts() {
        return postRepository.findAll().stream()
                .filter(post -> post.getStatus() == 0)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get paginated posts
     */
    public Map<String, Object> getPaginatedPosts(int page, int size) {
        List<Post> allPosts = getAllActivePosts();

        // Paginate
        int totalItems = allPosts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Post> paginatedPosts = (fromIndex < totalItems) ? allPosts.subList(fromIndex, toIndex) : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedPosts);
        response.put("totalElements", totalItems);
        response.put("totalPages", totalPages);
        response.put("size", size);
        response.put("number", page - 1); // 0-based page index for API consistency

        return response;
    }

    /**
     * Get post by ID
     */
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    /**
     * Get post by slug
     */
    public Optional<Post> getPostBySlug(String slug) {
        return getAllActivePosts().stream()
                .filter(post -> post.getSlug().equals(slug))
                .findFirst();
    }

    /**
     * Get post details with related posts
     */
    public Map<String, Object> getPostDetailsWithRelated(String slug) {
        Optional<Post> postOpt = getPostBySlug(slug);
        if (postOpt.isEmpty()) {
            return null;
        }

        Post post = postOpt.get();
        Long topicId = post.getTopicId();

        // Get related posts from same topic (excluding current post)
        List<Post> relatedPosts = getAllActivePosts().stream()
                .filter(p -> p.getTopicId().equals(topicId) && !p.getSlug().equals(slug))
                .limit(9)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("list_post", relatedPosts);

        return response;
    }

    /**
     * Get posts by topic ID
     */
    public List<Post> getPostsByTopicId(Long topicId) {
        return getAllActivePosts().stream()
                .filter(post -> post.getTopicId().equals(topicId))
                .collect(Collectors.toList());
    }

    /**
     * Get posts by topic
     */
    public Map<String, Object> getPostsByTopic(String slug, int page, int size) {
        Optional<Topic> topicOpt = topicRepository.findBySlug(slug);
        if (topicOpt.isEmpty()) {
            return null;
        }

        Topic topic = topicOpt.get();
        Long topicId = topic.getId();

        // Get all posts for the topic
        List<Post> allTopicPosts = getAllActivePosts().stream()
                .filter(post -> post.getTopicId().equals(topicId))
                .collect(Collectors.toList());

        // Paginate
        int totalItems = allTopicPosts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Post> paginatedPosts = (fromIndex < totalItems) ? allTopicPosts.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedPosts);
        response.put("totalElements", totalItems);
        response.put("totalPages", totalPages);
        response.put("size", size);
        response.put("number", page - 1); // 0-based page index for API consistency

        return response;
    }

    /**
     * Get posts by type
     */
    public List<Post> getPostsByType(String type) {
        return getAllActivePosts().stream()
                .filter(post -> post.getType().equals(type))
                .collect(Collectors.toList());
    }

    /**
     * Create a new post
     */
    public Post createPost(Post post, MultipartFile imageFile) throws IOException {
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "posts");
            post.setImage(fileName);
        }

        post.setCreatedAt(new Date());
        return postRepository.save(post);
    }

    /**
     * Update an existing post
     */
    public Post updatePost(Long id, Post postDetails, MultipartFile imageFile) throws IOException {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return null;
        }

        Post post = optionalPost.get();

        // Update post fields
        post.setTitle(postDetails.getTitle());
        post.setSlug(postDetails.getSlug());
        post.setDetail(postDetails.getDetail());
        post.setTopicId(postDetails.getTopicId());
        post.setType(postDetails.getType());
        post.setStatus(postDetails.getStatus());
        post.setUpdatedAt(new Date());

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "posts");
            post.setImage(fileName);
        }

        return postRepository.save(post);
    }

    /**
     * Update post with base64 image
     */
    public Post updatePostWithBase64Image(Long id, Post postDetails, String imageBase64) throws IOException {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return null;
        }

        Post post = optionalPost.get();

        // Update post fields
        post.setTitle(postDetails.getTitle());
        post.setSlug(postDetails.getSlug());
        post.setDetail(postDetails.getDetail());
        post.setTopicId(postDetails.getTopicId());
        post.setType(postDetails.getType());
        post.setStatus(postDetails.getStatus());
        post.setUpdatedAt(new Date());

        // Handle base64 image if provided
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            String fileName = fileStorageService.saveBase64Image(imageBase64, "posts");
            post.setImage(fileName);
        }

        return postRepository.save(post);
    }

    /**
     * Delete post
     */
    public boolean deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            return false;
        }

        postRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete post (set status to 0)
     */
    public Post softDeletePost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return null;
        }

        Post post = optionalPost.get();
        post.setStatus(0);
        post.setUpdatedAt(new Date());

        return postRepository.save(post);
    }

    /**
     * Restore soft-deleted post (set status to 1)
     */
    public Post restorePost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return null;
        }

        Post post = optionalPost.get();
        post.setStatus(1);
        post.setUpdatedAt(new Date());

        return postRepository.save(post);
    }

    /**
     * Toggle post status between 1 and 2
     */
    public Post togglePostStatus(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return null;
        }

        Post post = optionalPost.get();
        post.setStatus(post.getStatus() == 1 ? 2 : 1);
        post.setUpdatedAt(new Date());

        return postRepository.save(post);
    }
}
package com.example.controller.backend;

import com.example.entity.Post;
import com.example.service.PostService;
import com.example.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Post>> index() {
        List<Post> posts = postService.getAllActivePosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("topic_id") Long topicId,
            @RequestParam("title") String title,
            @RequestParam("slug") String slug,
            @RequestParam("detail") String detail,
            @RequestParam("type") String type,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Post post = new Post();
            post.setTopicId(topicId);
            post.setTitle(title);
            post.setSlug(slug);
            post.setDetail(detail);
            post.setType(type);
            post.setMetakey(metakey);
            post.setMetadesc(metadesc);
            post.setStatus(status);
            post.setCreatedBy(1); // Replace with actual authentication logic for user ID

            postService.createPost(post, imageFile);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Post created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(post.get());
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("topic_id") Long topicId,
            @RequestParam("title") String title,
            @RequestParam("slug") String slug,
            @RequestParam("detail") String detail,
            @RequestParam("type") String type,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageBase64", required = false) String imageBase64) {

        try {
            Optional<Post> optionalPost = postService.getPostById(id);
            if (optionalPost.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Post not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Post postDetails = new Post();
            postDetails.setTopicId(topicId);
            postDetails.setTitle(title);
            postDetails.setSlug(slug);
            postDetails.setDetail(detail);
            postDetails.setType(type);
            postDetails.setMetakey(metakey);
            postDetails.setMetadesc(metadesc);
            postDetails.setStatus(status);
            postDetails.setUpdatedBy(1); // Replace with actual authentication logic for user ID

            Post updatedPost;

            if (imageFile != null && !imageFile.isEmpty()) {
                updatedPost = postService.updatePost(id, postDetails, imageFile);
            } else if (imageBase64 != null && !imageBase64.isEmpty()) {
                updatedPost = postService.updatePostWithBase64Image(id, postDetails, imageBase64);
            } else {
                // No image update
                updatedPost = postService.updatePost(id, postDetails, null);
            }

            if (updatedPost != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Post updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update post");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean isDeleted = postService.deletePost(id);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        Post updatedPost = postService.togglePostStatus(id);
        if (updatedPost != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post status updated successfully");
            response.put("status", String.valueOf(updatedPost.getStatus()));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Post deletedPost = postService.softDeletePost(id);
        if (deletedPost != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post moved to trash successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of trashed resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Post>> trash() {
        List<Post> trashedPosts = postService.getAllTrashedPosts();
        return ResponseEntity.ok(trashedPosts);
    }

    /**
     * Restore the specified resource from trash.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Post restoredPost = postService.restorePost(id);
        if (restoredPost != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post restored successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get posts by topic ID.
     */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<Post>> getByTopicId(@PathVariable Long topicId) {
        List<Post> posts = postService.getPostsByTopicId(topicId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get posts by type.
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Post>> getByType(@PathVariable String type) {
        List<Post> posts = postService.getPostsByType(type);
        return ResponseEntity.ok(posts);
    }
}
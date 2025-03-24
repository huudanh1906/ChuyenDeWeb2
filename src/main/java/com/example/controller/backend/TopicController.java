package com.example.controller.backend;

import com.example.entity.Topic;
import com.example.service.TopicService;
import com.example.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/topics")
@CrossOrigin(origins = "*")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Topic>> index() {
        List<Topic> topics = topicService.getAllActiveTopics();
        return ResponseEntity.ok(topics);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam("slug") String slug,
            @RequestParam("parent_id") Long parentId,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Topic topic = new Topic();
            topic.setName(name);
            topic.setSlug(slug);
            topic.setParentId(parentId);
            topic.setMetakey(metakey);
            topic.setMetadesc(metadesc);
            topic.setStatus(status);

            topicService.createTopic(topic, imageFile);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Topic created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating topic: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Topic> topic = topicService.getTopicById(id);
        if (topic.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Topic not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(topic.get());
    }

    /**
     * Get topic for editing along with parent topics.
     */
    @GetMapping("/{id}/edit")
    public ResponseEntity<?> edit(@PathVariable Long id) {
        Optional<Topic> topic = topicService.getTopicById(id);
        if (topic.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Topic not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("topic", topic.get());
        // Get a list of parent topics (not including itself)
        List<Topic> parentTopics = topicService.getParentTopics(id);
        result.put("parentTopics", parentTopics);

        return ResponseEntity.ok(result);
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("slug") String slug,
            @RequestParam("parent_id") Long parentId,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageBase64", required = false) String imageBase64) {

        try {
            Optional<Topic> optionalTopic = topicService.getTopicById(id);
            if (optionalTopic.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Topic not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Topic topicDetails = new Topic();
            topicDetails.setName(name);
            topicDetails.setSlug(slug);
            topicDetails.setParentId(parentId);
            topicDetails.setMetakey(metakey);
            topicDetails.setMetadesc(metadesc);
            topicDetails.setStatus(status);

            Topic updatedTopic = topicService.updateTopic(id, topicDetails, imageFile, imageBase64);

            if (updatedTopic != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Topic updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update topic");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating topic: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean isDeleted = topicService.deleteTopic(id);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Topic deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Topic not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        Topic updatedTopic = topicService.toggleTopicStatus(id);
        if (updatedTopic != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Topic status updated successfully");
            response.put("status", String.valueOf(updatedTopic.getStatus()));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Topic not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Topic deletedTopic = topicService.softDeleteTopic(id);
        if (deletedTopic != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Topic moved to trash successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Topic not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of trashed resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Topic>> trash() {
        List<Topic> trashedTopics = topicService.getAllTrashedTopics();
        return ResponseEntity.ok(trashedTopics);
    }

    /**
     * Restore the specified resource from trash.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Topic restoredTopic = topicService.restoreTopic(id);
        if (restoredTopic != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Topic restored successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Topic not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get child topics of a specific parent.
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Topic>> getByParentId(@PathVariable Long parentId) {
        List<Topic> topics = topicService.getTopicsByParentId(parentId);
        return ResponseEntity.ok(topics);
    }
}
package com.example.service;

import com.example.entity.Topic;
import com.example.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all active topics (status != 0)
     */
    public List<Topic> getAllActiveTopics() {
        return topicRepository.findAll().stream()
                .filter(topic -> topic.getStatus() != 0)
                .sorted(Comparator.comparing(Topic::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all trashed topics (status = 0)
     */
    public List<Topic> getAllTrashedTopics() {
        return topicRepository.findByStatus(0);
    }

    /**
     * Get a topic by id
     */
    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }

    /**
     * Get a topic by slug
     */
    public Optional<Topic> getTopicBySlug(String slug) {
        return topicRepository.findBySlug(slug);
    }

    /**
     * Get topics by parent id
     */
    public List<Topic> getTopicsByParentId(Long parentId) {
        return topicRepository.findByParentId(parentId);
    }

    /**
     * Get parent topics (excluding a specific topic)
     */
    public List<Topic> getParentTopics(Long excludeId) {
        return topicRepository.findAll().stream()
                .filter(t -> t.getStatus() != 0 && !t.getId().equals(excludeId))
                .collect(Collectors.toList());
    }

    /**
     * Create a new topic
     */
    public Topic createTopic(Topic topic, MultipartFile imageFile) throws IOException {
        // Set image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "topics");
            // If Topic entity has an image field, would set it here
            // topic.setImage(fileName);
        }

        topic.setCreatedAt(new Date());
        topic.setCreatedBy(1); // Replace with actual authentication logic for user ID

        return topicRepository.save(topic);
    }

    /**
     * Update an existing topic
     */
    public Topic updateTopic(Long id, Topic topicDetails, MultipartFile imageFile, String imageBase64)
            throws IOException {
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if (optionalTopic.isPresent()) {
            Topic existingTopic = optionalTopic.get();

            existingTopic.setName(topicDetails.getName());
            existingTopic.setSlug(topicDetails.getSlug());
            existingTopic.setParentId(topicDetails.getParentId());
            existingTopic.setMetakey(topicDetails.getMetakey());
            existingTopic.setMetadesc(topicDetails.getMetadesc());
            existingTopic.setStatus(topicDetails.getStatus());

            // Handle image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile, "topics");
                // If Topic entity has an image field, would set it here
                // existingTopic.setImage(fileName);
            } else if (imageBase64 != null && !imageBase64.isEmpty() && imageBase64.startsWith("data:image")) {
                String fileName = fileStorageService.saveBase64Image(imageBase64, "topics");
                // If Topic entity has an image field, would set it here
                // existingTopic.setImage(fileName);
            }

            existingTopic.setUpdatedAt(new Date());
            existingTopic.setUpdatedBy(1); // Replace with actual authentication logic for user ID

            return topicRepository.save(existingTopic);
        }
        return null;
    }

    /**
     * Delete a topic permanently
     */
    public boolean deleteTopic(Long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        if (topic.isPresent()) {
            topicRepository.delete(topic.get());
            return true;
        }
        return false;
    }

    /**
     * Toggle topic status
     */
    public Topic toggleTopicStatus(Long id) {
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            topic.setStatus(topic.getStatus() == 1 ? 2 : 1);
            topic.setUpdatedAt(new Date());
            topic.setUpdatedBy(1); // Replace with actual authentication logic for user ID
            return topicRepository.save(topic);
        }
        return null;
    }

    /**
     * Soft delete a topic (set status to 0)
     */
    public Topic softDeleteTopic(Long id) {
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            topic.setStatus(0);
            topic.setUpdatedAt(new Date());
            topic.setUpdatedBy(1); // Replace with actual authentication logic for user ID
            return topicRepository.save(topic);
        }
        return null;
    }

    /**
     * Restore a trashed topic
     */
    public Topic restoreTopic(Long id) {
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            topic.setStatus(2);
            topic.setUpdatedAt(new Date());
            topic.setUpdatedBy(1); // Replace with actual authentication logic for user ID
            return topicRepository.save(topic);
        }
        return null;
    }
}
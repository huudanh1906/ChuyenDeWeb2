package com.example.controller.frontend;

import com.example.entity.Contact;
import com.example.service.ContactService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@RequestMapping("/api/frontend/contacts")
@CrossOrigin(origins = "*")
public class FrontendContactController {

    private static final Logger logger = LoggerFactory.getLogger(FrontendContactController.class);

    @Autowired
    private ContactService contactService;

    /**
     * Store a contact message
     */
    @PostMapping
    public ResponseEntity<?> contact(@RequestBody Map<String, Object> requestData) {
        logger.info("Contact request data: {}", requestData);

        String name = requestData.get("name") != null ? requestData.get("name").toString() : "";
        String email = requestData.get("email") != null ? requestData.get("email").toString() : "";
        String phone = requestData.get("phone") != null ? requestData.get("phone").toString() : "";
        String title = requestData.get("title") != null ? requestData.get("title").toString() : "";
        String content = requestData.get("content") != null ? requestData.get("content").toString() : "";

        // Validate request data
        Map<String, String> errors = contactService.validateContact(name, email, phone, title, content);

        if (!errors.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("errors", errors);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        }

        // Save the contact message
        try {
            Contact contact = new Contact();
            contact.setName(name);
            contact.setEmail(email);
            contact.setPhone(phone);
            contact.setTitle(title);
            contact.setContent(content);
            contact.setStatus(1);
            contact.setReplayId(0); // Default value for new contacts

            contactService.createContact(contact);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Contact message sent successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to save contact: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
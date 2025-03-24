package com.example.controller.backend;

import com.example.entity.Contact;
import com.example.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactService contactService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Contact>> index() {
        List<Contact> contacts = contactService.getAllActiveContacts();
        return ResponseEntity.ok(contacts);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "status", defaultValue = "2") int status) {

        try {
            // Validate contact data
            Map<String, String> errors = contactService.validateContact(name, email, phone, title, content);

            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
            }

            Contact contact = new Contact();
            contact.setName(name);
            contact.setEmail(email);
            contact.setPhone(phone);
            contact.setTitle(title);
            contact.setContent(content);
            contact.setStatus(status);
            contact.setReplayId(0); // Default value for new contacts
            contact.setCreatedBy(1); // Replace with actual authentication logic for user ID

            contactService.createContact(contact);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Contact created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating contact: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Contact> contact = contactService.getContactById(id);
        if (contact.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Contact not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(contact.get());
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("replay_id") int replayId,
            @RequestParam("status") int status) {

        try {
            // Validate contact data
            Map<String, String> errors = contactService.validateContact(name, email, phone, title, content);

            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
            }

            Contact contactDetails = new Contact();
            contactDetails.setName(name);
            contactDetails.setEmail(email);
            contactDetails.setPhone(phone);
            contactDetails.setTitle(title);
            contactDetails.setContent(content);
            contactDetails.setReplayId(replayId);
            contactDetails.setStatus(status);
            contactDetails.setUpdatedBy(1); // Replace with actual authentication logic for user ID

            Contact updatedContact = contactService.updateContact(id, contactDetails);

            if (updatedContact == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Contact not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Contact updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating contact: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean deleted = contactService.deleteContact(id);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Contact destroyed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Contact not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Change the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        Contact contact = contactService.toggleContactStatus(id);

        Map<String, String> response = new HashMap<>();
        if (contact != null) {
            response.put("message", "Contact status changed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Contact not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Contact contact = contactService.softDeleteContact(id);

        Map<String, String> response = new HashMap<>();
        if (contact != null) {
            response.put("message", "Contact deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Contact not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of soft-deleted resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Contact>> trash() {
        List<Contact> contacts = contactService.getAllTrashedContacts();
        return ResponseEntity.ok(contacts);
    }

    /**
     * Restore a soft-deleted resource.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Contact contact = contactService.restoreContact(id);

        Map<String, String> response = new HashMap<>();
        if (contact != null) {
            response.put("message", "Contact restored successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Contact not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
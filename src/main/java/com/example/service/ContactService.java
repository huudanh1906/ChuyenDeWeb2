package com.example.service;

import com.example.entity.Contact;
import com.example.repository.ContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    /**
     * Get all active contacts
     */
    public List<Contact> getAllActiveContacts() {
        return contactRepository.findAll().stream()
                .filter(contact -> contact.getStatus() != 0)
                .sorted(Comparator.comparing(Contact::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all trashed contacts
     */
    public List<Contact> getAllTrashedContacts() {
        return contactRepository.findAll().stream()
                .filter(contact -> contact.getStatus() == 0)
                .sorted(Comparator.comparing(Contact::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get contact by ID
     */
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    /**
     * Create a new contact
     */
    public Contact createContact(Contact contact) {
        contact.setCreatedAt(new Date());
        return contactRepository.save(contact);
    }

    /**
     * Update an existing contact
     */
    public Contact updateContact(Long id, Contact contactDetails) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (optionalContact.isEmpty()) {
            return null;
        }

        Contact contact = optionalContact.get();
        contact.setName(contactDetails.getName());
        contact.setEmail(contactDetails.getEmail());
        contact.setPhone(contactDetails.getPhone());
        contact.setTitle(contactDetails.getTitle());
        contact.setContent(contactDetails.getContent());
        contact.setReplayId(contactDetails.getReplayId());
        contact.setStatus(contactDetails.getStatus());
        contact.setUpdatedAt(new Date());
        contact.setUpdatedBy(contactDetails.getUpdatedBy());

        return contactRepository.save(contact);
    }

    /**
     * Delete contact
     */
    public boolean deleteContact(Long id) {
        if (!contactRepository.existsById(id)) {
            return false;
        }

        contactRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete contact (set status to 0)
     */
    public Contact softDeleteContact(Long id) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (optionalContact.isEmpty()) {
            return null;
        }

        Contact contact = optionalContact.get();
        contact.setStatus(0);
        contact.setUpdatedAt(new Date());

        return contactRepository.save(contact);
    }

    /**
     * Restore soft-deleted contact
     */
    public Contact restoreContact(Long id) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (optionalContact.isEmpty()) {
            return null;
        }

        Contact contact = optionalContact.get();
        contact.setStatus(2);
        contact.setUpdatedAt(new Date());

        return contactRepository.save(contact);
    }

    /**
     * Toggle contact status
     */
    public Contact toggleContactStatus(Long id) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (optionalContact.isEmpty()) {
            return null;
        }

        Contact contact = optionalContact.get();
        contact.setStatus(contact.getStatus() == 1 ? 2 : 1);
        contact.setUpdatedAt(new Date());

        return contactRepository.save(contact);
    }

    /**
     * Validate contact form
     */
    public Map<String, String> validateContact(String name, String email, String phone, String title, String content) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.isEmpty()) {
            errors.put("name", "Name is required");
        } else if (name.length() > 255) {
            errors.put("name", "Name cannot exceed 255 characters");
        }

        if (email == null || email.isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!isValidEmail(email)) {
            errors.put("email", "Email is invalid");
        } else if (email.length() > 255) {
            errors.put("email", "Email cannot exceed 255 characters");
        }

        if (phone == null || phone.isEmpty()) {
            errors.put("phone", "Phone is required");
        } else if (phone.length() > 15) {
            errors.put("phone", "Phone cannot exceed 15 characters");
        }

        if (title == null || title.isEmpty()) {
            errors.put("title", "Title is required");
        } else if (title.length() > 255) {
            errors.put("title", "Title cannot exceed 255 characters");
        }

        if (content == null || content.isEmpty()) {
            errors.put("content", "Content is required");
        }

        return errors;
    }

    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
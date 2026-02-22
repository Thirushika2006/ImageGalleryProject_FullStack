package com.example.gallery.controller;

import com.example.gallery.entity.User;
import com.example.gallery.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ImageService imageService;

    // Get all users with their image count and storage
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = imageService.getAllUsers();

        List<Map<String, Object>> result = users.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("role", user.getRole());
            // Only metadata — no photos
            map.put("imageCount", imageService.getImageCount(user.getUsername()));
            map.put("storageUsed", formatStorage(imageService.getTotalStorage(user.getUsername())));
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    // Delete any user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        imageService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    // Helper - format storage
    private String formatStorage(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }
}
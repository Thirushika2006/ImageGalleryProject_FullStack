package com.example.gallery.controller;

import com.example.gallery.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ImageService imageService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfile(Authentication authentication) {
        String username = authentication.getName();

        long imageCount = imageService.getImageCount(username);
        long totalBytes = imageService.getTotalStorage(username);

        // Format storage nicely
        String storageUsed;
        if (totalBytes < 1024) storageUsed = totalBytes + " B";
        else if (totalBytes < 1024 * 1024) storageUsed = String.format("%.1f KB", totalBytes / 1024.0);
        else storageUsed = String.format("%.1f MB", totalBytes / (1024.0 * 1024));

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", username);
        profile.put("imageCount", imageCount);
        profile.put("storageUsed", storageUsed);

        return ResponseEntity.ok(profile);
    }
}
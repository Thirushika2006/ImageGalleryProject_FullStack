package com.example.gallery.controller;

import com.example.gallery.dto.ImageDTO;
import com.example.gallery.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        imageService.uploadImage(file, authentication.getName());
        return ResponseEntity.ok("Upload successful!");
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserImages(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "") String search) {

        Page<ImageDTO> imagePage = imageService.getImagesByUsername(
                authentication.getName(), page, size, search);

        Map<String, Object> response = new HashMap<>();
        response.put("images", imagePage.getContent());
        response.put("currentPage", imagePage.getNumber());
        response.put("totalPages", imagePage.getTotalPages());
        response.put("totalItems", imagePage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteImage(
            @PathVariable Long id, Authentication authentication) {
        imageService.deleteImage(id, authentication.getName());
        return ResponseEntity.ok("Moved to trash!");
    }

    @PutMapping("/rename/{id}")
    public ResponseEntity<String> renameImage(
            @PathVariable Long id,
            @RequestParam String newName,
            Authentication authentication) {
        imageService.renameImage(id, newName, authentication.getName());
        return ResponseEntity.ok("Renamed successfully!");
    }

    // ✅ Download via Cloudinary URL
    @GetMapping("/download/{id}")
    public ResponseEntity<String> downloadImage(
            @PathVariable Long id, Authentication authentication) {
        try {
            String cloudinaryUrl = imageService.getImageUrlForDownload(
                    id, authentication.getName());
            return ResponseEntity.ok(cloudinaryUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Download failed");
        }
    }

    // Trash endpoints
    @GetMapping("/trash")
    public ResponseEntity<List<ImageDTO>> getTrash(Authentication authentication) {
        return ResponseEntity.ok(imageService.getTrashImages(authentication.getName()));
    }

    @PutMapping("/trash/restore/{id}")
    public ResponseEntity<String> restoreImage(
            @PathVariable Long id, Authentication authentication) {
        imageService.restoreImage(id, authentication.getName());
        return ResponseEntity.ok("Image restored!");
    }

    @DeleteMapping("/trash/permanent/{id}")
    public ResponseEntity<String> permanentDelete(
            @PathVariable Long id, Authentication authentication) throws IOException {
        imageService.permanentDelete(id, authentication.getName());
        return ResponseEntity.ok("Permanently deleted!");
    }
}
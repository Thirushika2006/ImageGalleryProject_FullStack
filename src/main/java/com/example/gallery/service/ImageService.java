package com.example.gallery.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.gallery.dto.ImageDTO;
import com.example.gallery.entity.Image;
import com.example.gallery.entity.User;
import com.example.gallery.repository.ImageRepository;
import com.example.gallery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Cloudinary cloudinary;

    // ✅ Upload to Cloudinary
    public void uploadImage(MultipartFile file, String username) throws IOException {
        if (file.isEmpty()) throw new RuntimeException("File is empty");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Upload to Cloudinary — stored in folder named after username
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "gallery/" + username,
                        "resource_type", "image"
                )
        );

        // Get Cloudinary URL and public ID
        String cloudinaryUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setPath(cloudinaryUrl);         // full Cloudinary URL
        image.setCloudinaryPublicId(publicId); // for deletion
        image.setFileType(file.getContentType());
        image.setFileSize(file.getSize());
        image.setUploadTime(LocalDateTime.now());
        image.setDeleted(false);
        image.setUser(user);

        imageRepository.save(image);
        System.out.println("Uploaded to Cloudinary: " + cloudinaryUrl);
    }

    // Get active images with pagination + search
    public Page<ImageDTO> getImagesByUsername(String username, int page, int size, String search) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadTime").descending());
        Page<Image> images;

        if (search != null && !search.trim().isEmpty()) {
            images = imageRepository.findByUserIdAndDeletedAndNameContainingIgnoreCase(
                    user.getId(), false, search.trim(), pageable);
        } else {
            images = imageRepository.findByUserIdAndDeleted(user.getId(), false, pageable);
        }

        return images.map(this::toDTO);
    }

    // Soft delete → trash
    public void deleteImage(Long id, String username) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        verifyOwner(image, username);
        image.setDeleted(true);
        image.setDeletedAt(LocalDateTime.now());
        imageRepository.save(image);
    }

    // Get trash images
    public List<ImageDTO> getTrashImages(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return imageRepository.findByUserIdAndDeleted(user.getId(), true)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Restore from trash
    public void restoreImage(Long id, String username) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        verifyOwner(image, username);
        image.setDeleted(false);
        image.setDeletedAt(null);
        imageRepository.save(image);
    }

    // ✅ Permanent delete — removes from Cloudinary too
    public void permanentDelete(Long id, String username) throws IOException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        verifyOwner(image, username);

        // Delete from Cloudinary
        if (image.getCloudinaryPublicId() != null) {
            cloudinary.uploader().destroy(
                    image.getCloudinaryPublicId(),
                    ObjectUtils.emptyMap()
            );
        }

        imageRepository.deleteById(id);
    }

    // Rename (only in database — Cloudinary URL stays same)
    public void renameImage(Long id, String newName, String username) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        verifyOwner(image, username);

        String extension = "";
        int dot = image.getName().lastIndexOf(".");
        if (dot > 0) extension = image.getName().substring(dot);

        image.setName(newName + extension);
        imageRepository.save(image);
    }

    // Download — returns Cloudinary URL directly
    public String getImageUrlForDownload(Long id, String username) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        User imageOwner = userRepository.findById(image.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!imageOwner.getUsername().equals(username)) {
            throw new RuntimeException("No permission to download");
        }
        return image.getPath(); // Cloudinary URL
    }

    // Profile stats
    public long getImageCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return imageRepository.countByUserIdAndDeleted(user.getId(), false);
    }

    public long getTotalStorage(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return imageRepository.sumFileSizeByUserId(user.getId());
    }

    // Admin
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    private ImageDTO toDTO(Image img) {
        return new ImageDTO(
                img.getId(),
                img.getName(),
                img.getPath(),   // Cloudinary URL
                img.getFileType(),
                img.getFileSize(),
                img.getUploadTime()
        );
    }

    private void verifyOwner(Image image, String username) {
        User owner = userRepository.findById(image.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!owner.getUsername().equals(username)) {
            throw new RuntimeException("No permission");
        }
    }
}
package com.example.gallery.repository;

import com.example.gallery.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // Active images (not deleted) with pagination
    Page<Image> findByUserIdAndDeleted(Long userId, boolean deleted, Pageable pageable);

    // Active images with search + pagination
    Page<Image> findByUserIdAndDeletedAndNameContainingIgnoreCase(
            Long userId, boolean deleted, String name, Pageable pageable);

    // Deleted images (trash)
    List<Image> findByUserIdAndDeleted(Long userId, boolean deleted);

    // Count active images
    long countByUserIdAndDeleted(Long userId, boolean deleted);

    // Total storage of active images
    @Query("SELECT COALESCE(SUM(i.fileSize), 0) FROM Image i WHERE i.user.id = :userId AND i.deleted = false")
    long sumFileSizeByUserId(Long userId);

    // Admin - all images across all users
    Page<Image> findAllByDeleted(boolean deleted, Pageable pageable);

    // Keep for compatibility
    List<Image> findByUserId(Long userId);
}
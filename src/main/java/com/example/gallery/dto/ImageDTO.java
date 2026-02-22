package com.example.gallery.dto;

import java.time.LocalDateTime;

public class ImageDTO {

    private Long id;
    private String name;
    private String path;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadTime;

    // Constructor
    public ImageDTO(Long id, String name, String path, String fileType,
                    Long fileSize, LocalDateTime uploadTime) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPath() { return path; }
    public String getFileType() { return fileType; }
    public Long getFileSize() { return fileSize; }
    public LocalDateTime getUploadTime() { return uploadTime; }
}
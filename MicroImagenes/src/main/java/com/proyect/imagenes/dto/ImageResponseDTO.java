package com.proyect.imagenes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDTO {
    private String id;
    private String imageId;
    private String fileName;
    private String contentType;
    private Long size;
    private LocalDateTime uploadDate;
    private String userId;
    private String userEmail;
    private String petId;
    private String title;
    private String description;
    private String category;
    private boolean active;
    private String downloadUrl;
}
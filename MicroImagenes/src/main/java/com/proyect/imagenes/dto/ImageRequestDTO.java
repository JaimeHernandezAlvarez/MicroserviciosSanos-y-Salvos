package com.proyect.imagenes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequestDTO {
    
    @NotBlank(message = "Image ID is required")
    private String imageId;
    
    @NotNull(message = "File is required")
    private MultipartFile file;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "User email is required")
    private String userEmail;
    
    private String petId;
    private String title;
    private String description;
    private String category;
}
package com.proyect.imagenes.service;

import com.proyect.imagenes.dto.ImageRequestDTO;
import com.proyect.imagenes.dto.ImageResponseDTO;
import com.proyect.imagenes.Exceptions.ImageNotFoundException;
import com.proyect.imagenes.Exceptions.ImageAlreadyExistsException;
import com.proyect.imagenes.model.Image;
import com.proyect.imagenes.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    
    private final ImageRepository imageRepository;
    
    public ImageResponseDTO saveImage(ImageRequestDTO request) throws IOException {
        if (imageRepository.existsByImageId(request.getImageId())) {
            throw new ImageAlreadyExistsException("Image with ID " + request.getImageId() + " already exists");
        }
        
        MultipartFile file = request.getFile();
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
        
        Image image = Image.builder()
                .imageId(request.getImageId())
                .fileName(file.getOriginalFilename())
                .contentType(contentType)
                .data(file.getBytes())
                .size(file.getSize())
                .build();
        
        Image savedImage = imageRepository.save(image);
        log.info("Image saved successfully with imageId: {}", savedImage.getImageId());
        
        return convertToResponseDTO(savedImage);
    }
    
    public ImageResponseDTO getImageById(String imageId) {
        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));
        return convertToResponseDTO(image);
    }
    
    public byte[] downloadImage(String imageId) {
        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));
        return image.getData();
    }
    
    public void deleteImage(String imageId) {
        if (!imageRepository.existsByImageId(imageId)) {
            throw new ImageNotFoundException("Image not found with ID: " + imageId);
        }
        imageRepository.deleteByImageId(imageId);
        log.info("Image deleted successfully with imageId: {}", imageId);
    }
    
    public boolean imageExists(String imageId) {
        return imageRepository.existsByImageId(imageId);
    }
    
    private ImageResponseDTO convertToResponseDTO(Image image) {
        return ImageResponseDTO.builder()
                .id(image.getId())
                .imageId(image.getImageId())
                .fileName(image.getFileName())
                .contentType(image.getContentType())
                .size(image.getSize())
                .uploadDate(image.getUploadDate())
                .downloadUrl("http://localhost:8081/api/images/download/" + image.getImageId())
                .build();
    }
}
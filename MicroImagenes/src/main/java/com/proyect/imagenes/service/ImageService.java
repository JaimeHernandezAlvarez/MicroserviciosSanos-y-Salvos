package com.proyect.imagenes.service;

import com.proyect.imagenes.dto.ImageRequestDTO;
import com.proyect.imagenes.dto.ImageResponseDTO;
import com.proyect.imagenes.Exceptions.ImageNotFoundException;
import com.proyect.imagenes.Exceptions.ImageAlreadyExistsException;
import com.proyect.imagenes.Exceptions.UnauthorizedAccessException;
import com.proyect.imagenes.model.Image;
import com.proyect.imagenes.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
                .userId(request.getUserId())
                .userEmail(request.getUserEmail())
                .petId(request.getPetId())
                .title(request.getTitle() != null ? request.getTitle() : file.getOriginalFilename())
                .description(request.getDescription())
                .category(request.getCategory() != null ? request.getCategory() : "general")
                .active(true)
                .build();
        
        Image savedImage = imageRepository.save(image);
        log.info("Image saved - ID: {}, User: {}, Pet: {}", savedImage.getImageId(), savedImage.getUserId(), savedImage.getPetId());
        
        return convertToResponseDTO(savedImage);
    }
    
    public ImageResponseDTO getImageById(String imageId) {
        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));
        return convertToResponseDTO(image);
    }
    
    public byte[] downloadImage(String imageId, String userId) {
        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));
        
        if (!image.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("User not authorized to access this image");
        }
        
        return image.getData();
    }
    
    public List<ImageResponseDTO> getImagesByUserId(String userId) {
        return imageRepository.findByUserId(userId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<ImageResponseDTO> getImagesByPetId(String petId) {
        return imageRepository.findByPetId(petId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<ImageResponseDTO> getImagesByUserAndCategory(String userId, String category) {
        return imageRepository.findByUserIdAndCategory(userId, category).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<ImageResponseDTO> getAllImages() {
        return imageRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public void deleteImage(String imageId, String userId) {
        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));
        
        if (!image.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("User not authorized to delete this image");
        }
        
        imageRepository.deleteByImageId(imageId);
        log.info("Image deleted - ID: {}, User: {}", imageId, userId);
    }
    
    public void deleteImagesByUserId(String userId) {
        imageRepository.deleteByUserId(userId);
        log.info("All images deleted for user: {}", userId);
    }
    
    public boolean imageExists(String imageId) {
        return imageRepository.existsByImageId(imageId);
    }
    
    public long countUserImages(String userId) {
        return imageRepository.countByUserId(userId);
    }
    
    private ImageResponseDTO convertToResponseDTO(Image image) {
        return ImageResponseDTO.builder()
                .id(image.getId())
                .imageId(image.getImageId())
                .fileName(image.getFileName())
                .contentType(image.getContentType())
                .size(image.getSize())
                .uploadDate(image.getUploadDate())
                .userId(image.getUserId())
                .userEmail(image.getUserEmail())
                .petId(image.getPetId())
                .title(image.getTitle())
                .description(image.getDescription())
                .category(image.getCategory())
                .active(image.isActive())
                .downloadUrl("http://localhost:8081/api/images/download/" + image.getImageId())
                .build();
    }
}
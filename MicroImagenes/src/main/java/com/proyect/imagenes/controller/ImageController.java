package com.proyect.imagenes.controller;

import com.proyect.imagenes.assembler.ImageAssembler;
import com.proyect.imagenes.dto.ImageRequestDTO;
import com.proyect.imagenes.dto.ImageResponseDTO;
import com.proyect.imagenes.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Image Management", description = "Endpoints for managing images")
public class ImageController {
    
    private final ImageService imageService;
    private final ImageAssembler imageAssembler;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a new image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Image ID already exists")
    })
    public ResponseEntity<EntityModel<ImageResponseDTO>> uploadImage(
            @RequestParam("imageId") String imageId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("userEmail") String userEmail,
            @RequestParam(value = "petId", required = false) String petId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category) throws IOException {
        
        ImageRequestDTO request = ImageRequestDTO.builder()
                .imageId(imageId)
                .file(file)
                .userId(userId)
                .userEmail(userEmail)
                .petId(petId)
                .title(title)
                .description(description)
                .category(category)
                .build();
        
        ImageResponseDTO response = imageService.saveImage(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            EntityModel.of(response)
        );
    }
    
    @GetMapping("/{imageId}")
    @Operation(summary = "Get image metadata")
    public ResponseEntity<EntityModel<ImageResponseDTO>> getImageById(
            @PathVariable String imageId) {
        
        ImageResponseDTO image = imageService.getImageById(imageId);
        return ResponseEntity.ok(EntityModel.of(image));
    }
    
    @GetMapping("/download/{imageId}")
    @Operation(summary = "Download image file")
    public ResponseEntity<byte[]> downloadImage(
            @PathVariable String imageId,
            @RequestParam String userId) {
        
        ImageResponseDTO imageInfo = imageService.getImageById(imageId);
        byte[] imageData = imageService.downloadImage(imageId, userId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageInfo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageInfo.getFileName() + "\"")
                .body(imageData);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all images by user ID")
    public ResponseEntity<CollectionModel<EntityModel<ImageResponseDTO>>> getImagesByUser(
            @PathVariable String userId) {
        
        List<EntityModel<ImageResponseDTO>> images = imageService.getImagesByUserId(userId)
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CollectionModel.of(images));
    }
    
    @GetMapping("/pet/{petId}")
    @Operation(summary = "Get all images by pet ID")
    public ResponseEntity<CollectionModel<EntityModel<ImageResponseDTO>>> getImagesByPet(
            @PathVariable String petId) {
        
        List<EntityModel<ImageResponseDTO>> images = imageService.getImagesByPetId(petId)
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CollectionModel.of(images));
    }
    
    @GetMapping("/user/{userId}/category/{category}")
    @Operation(summary = "Get images by user and category")
    public ResponseEntity<CollectionModel<EntityModel<ImageResponseDTO>>> getImagesByUserAndCategory(
            @PathVariable String userId,
            @PathVariable String category) {
        
        List<EntityModel<ImageResponseDTO>> images = imageService.getImagesByUserAndCategory(userId, category)
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CollectionModel.of(images));
    }
    
    @GetMapping
    @Operation(summary = "Get all images")
    public ResponseEntity<CollectionModel<EntityModel<ImageResponseDTO>>> getAllImages() {
        List<EntityModel<ImageResponseDTO>> images = imageService.getAllImages()
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CollectionModel.of(images));
    }
    
    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete an image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Image not found"),
        @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    public ResponseEntity<Void> deleteImage(
            @PathVariable String imageId,
            @RequestParam String userId) {
        
        imageService.deleteImage(imageId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete all images of a user")
    public ResponseEntity<Void> deleteUserImages(@PathVariable String userId) {
        imageService.deleteImagesByUserId(userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{imageId}/exists")
    @Operation(summary = "Check if image exists")
    public ResponseEntity<Boolean> checkImageExists(@PathVariable String imageId) {
        return ResponseEntity.ok(imageService.imageExists(imageId));
    }
    
    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Count user images")
    public ResponseEntity<Long> countUserImages(@PathVariable String userId) {
        return ResponseEntity.ok(imageService.countUserImages(userId));
    }
}
package com.proyect.imagenes.controller;

import com.proyect.imagenes.assembler.ImageAssembler;
import com.proyect.imagenes.dto.ImageRequestDTO;
import com.proyect.imagenes.dto.ImageResponseDTO;
import com.proyect.imagenes.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Image Management", description = "Endpoints for managing images")
public class ImageController {
    
    private final ImageService imageService;
    private final ImageAssembler imageAssembler;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Image ID already exists")
    })
    public ResponseEntity<EntityModel<ImageResponseDTO>> uploadImage(
            @RequestParam("imageId") String imageId,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        ImageRequestDTO request = ImageRequestDTO.builder()
                .imageId(imageId)
                .file(file)
                .build();
        
        ImageResponseDTO response = imageService.saveImage(request);
        EntityModel<ImageResponseDTO> entityModel = imageAssembler.toModel(response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }
    
    @GetMapping("/{imageId}")
    @Operation(summary = "Get image metadata")
    public ResponseEntity<EntityModel<ImageResponseDTO>> getImageById(@PathVariable String imageId) {
        ImageResponseDTO image = imageService.getImageById(imageId);
        return ResponseEntity.ok(imageAssembler.toModel(image));
    }
    
    @GetMapping("/download/{imageId}")
    @Operation(summary = "Download image file")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String imageId) {
        ImageResponseDTO imageInfo = imageService.getImageById(imageId);
        byte[] imageData = imageService.downloadImage(imageId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageInfo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageInfo.getFileName() + "\"")
                .body(imageData);
    }
    
    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<Void> deleteImage(@PathVariable String imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{imageId}/exists")
    @Operation(summary = "Check if image exists")
    public ResponseEntity<Boolean> checkImageExists(@PathVariable String imageId) {
        return ResponseEntity.ok(imageService.imageExists(imageId));
    }
}
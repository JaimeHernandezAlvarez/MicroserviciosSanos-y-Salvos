package com.proyect.imagenes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "images")
public class Image {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String imageId;
    
    private String fileName;
    private String contentType;
    private byte[] data;
    private Long size;
    
    @Builder.Default
    private LocalDateTime uploadDate = LocalDateTime.now();
}
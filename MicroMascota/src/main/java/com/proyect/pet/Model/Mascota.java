package com.proyect.pet.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "mascotas")
public class Mascota {
    
    @Id
    private String id;
    
    private String name;
    private String species;
    private String breed;
    private String color;
    private String size;
    private String status; 
    private String imageId;
    
    private String ownerId;
    private String founderId;
    
    private String description;
    
    private LocalDateTime reportedAt;
    private LocalDateTime foundAt;
    private LocalDateTime reunitedAt;

    private Location lastLocation;
}
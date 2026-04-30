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
    
    // Estados: LOST, FOUND, REUNITED
    private String status; 
    
    private String imageId;
    
    // Referencias a los IDs del microservicio de Usuarios
    private String ownerId;
    private String founderId;
    
    private String description;
    
    // Fechas de registro de eventos
    private LocalDateTime reportedAt;
    private LocalDateTime foundAt;
    private LocalDateTime reunitedAt;

    // Ubicación
    private Location lastLocation;
}
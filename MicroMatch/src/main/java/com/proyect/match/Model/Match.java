package com.proyect.match.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "matches")
public class Match {
    
    @Id
    private String id;
    
    @Indexed
    private String lostPetId;      // ID de la mascota perdida
    
    @Indexed
    private String foundPetId;     // ID de la mascota encontrada
    
    private String ownerId;        // Dueño de la mascota perdida
    
    private String founderId;      // Quien encontró la mascota
    
    // Estados: PENDING, ACCEPTED, REJECTED, COMPLETED
    private String status;
    
    private double similarityScore; // Puntaje de similitud (0-100)
    
    private String matchReason;     // Por qué hicieron match
    
    private LocalDateTime matchedAt;
    private LocalDateTime respondedAt;
    
    private String ownerResponse;   // Mensaje del dueño
    private String founderResponse; // Mensaje del fundador
    
    @Builder.Default
    private boolean active = true;
}
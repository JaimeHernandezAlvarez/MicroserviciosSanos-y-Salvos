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
    private String lostPetId; 
    
    @Indexed
    private String foundPetId;     
    
    private String ownerId;        
    
    private String founderId;      
    
    private String status;
    
    private double similarityScore; 
    
    private String matchReason;
    
    private LocalDateTime matchedAt;
    private LocalDateTime respondedAt;
    
    private String ownerResponse;   
    private String founderResponse; 
    
    @Builder.Default
    private boolean active = true;
}
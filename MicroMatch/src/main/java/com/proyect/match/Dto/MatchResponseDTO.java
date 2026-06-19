package com.proyect.match.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDTO {
    private String id;
    private String lostPetId;
    private String foundPetId;
    private String ownerId;
    private String founderId;
    private String status;
    private double similarityScore;
    private String matchReason;
    private LocalDateTime matchedAt;
    private LocalDateTime respondedAt;
    private String lostPetName;
    private String foundPetName;
    private String lostPetImageId;
    private String foundPetImageId;
}
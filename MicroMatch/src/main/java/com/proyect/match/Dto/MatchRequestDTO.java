package com.proyect.match.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestDTO {
    private String lostPetId;
    private String foundPetId;
    private double similarityScore;
    private String matchReason;
}
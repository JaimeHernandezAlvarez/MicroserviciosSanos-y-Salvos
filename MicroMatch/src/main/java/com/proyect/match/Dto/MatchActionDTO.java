package com.proyect.match.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchActionDTO {
    private String matchId;
    private String action;      // ACCEPT, REJECT
    private String message;     // Mensaje opcional
    private String userId;      // ID del usuario que responde
}
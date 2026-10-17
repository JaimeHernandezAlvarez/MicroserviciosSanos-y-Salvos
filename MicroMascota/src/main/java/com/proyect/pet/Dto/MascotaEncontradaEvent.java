package com.proyect.pet.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaEncontradaEvent implements Serializable {
    private String mascotaId;
    private String ownerId;
    private String founderId;
    private String status;
}
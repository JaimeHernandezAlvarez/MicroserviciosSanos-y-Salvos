package com.proyect.pet.Model;

import lombok.Data;

@Data
public class Location {
    // Usamos Double para las coordenadas geográficas
    private Double latitude;
    private Double longitude;
    
    // Opcional: Un campo en texto para la dirección legible (ej: "Av. Siempre Viva 123")
    private String address; 
}
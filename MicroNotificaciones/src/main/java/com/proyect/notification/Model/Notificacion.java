package com.proyect.notification.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notificaciones")
public class Notificacion {

    @Id
    private String id;

    // ID del usuario que debe recibir esta notificación (el dueño de la campanita)
    @Indexed
    private String userId;

    private String title; // Ej: "¡Posible coincidencia encontrada!" o "Mascota vista cerca"
    private String message; // Ej: "Alguien reportó un perro similar a Firulais en tu área."

    // Tipos de notificación para mostrar distintos íconos en el frontend (MATCH, SYSTEM, NEARBY)
    private String type; 

    // Este es clave: Guarda el ID de la mascota o reporte asociado 
    // para que al hacer clic en la notificación, el frontend sepa a qué pantalla ir.
    private String relatedEntityId; 

    // Fundamental para saber si mostramos el "puntito rojo" en la campana
    private boolean isRead; 

    private LocalDateTime createdAt;
}
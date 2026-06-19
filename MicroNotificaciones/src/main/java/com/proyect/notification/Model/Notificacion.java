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

    @Indexed
    private String userId;

    private String title; 
    private String message; 

    private String type; 

    private String relatedEntityId; 

    private boolean isRead; 

    private LocalDateTime createdAt;
}
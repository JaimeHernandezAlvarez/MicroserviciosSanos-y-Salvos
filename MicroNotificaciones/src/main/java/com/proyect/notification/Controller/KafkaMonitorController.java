package com.proyect.notification.Controller;

import com.proyect.notification.Listener.KafkaConsumerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones/kafka-monitor")
// @CrossOrigin(origins = "*") <-- ELIMINA O COMENTA ESTA LÍNEA
public class KafkaMonitorController {

    @Autowired(required = false)
    private KafkaConsumerListener kafkaConsumerListener;

    @GetMapping
    public ResponseEntity<List<String>> obtenerStream() {
        try {
            if (kafkaConsumerListener == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            List<String> mensajes = kafkaConsumerListener.obtenerMensajesRecientes();
            
            if (mensajes == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            return ResponseEntity.ok(mensajes);
            
        } catch (Exception e) {
            System.out.println("Error al obtener mensajes de Kafka: " + e.getMessage());
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
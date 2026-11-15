package com.proyect.notification.Controller;

import com.proyect.notification.Listener.KafkaConsumerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones/kafka-monitor")
@CrossOrigin(origins = "*") // Permite que tu frontend consulte sin problemas de CORS
public class KafkaMonitorController {

    @Autowired
    private KafkaConsumerListener kafkaConsumerListener;

    @GetMapping
    public ResponseEntity<List<String>> obtenerStream() {
        return ResponseEntity.ok(kafkaConsumerListener.obtenerMensajesRecientes());
    }
}
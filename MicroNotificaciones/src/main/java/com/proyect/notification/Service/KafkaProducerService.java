package com.proyect.notification.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void enviarEvento(String mensaje) {
        kafkaTemplate.send("notificaciones-stream", mensaje);
        System.out.println("📤 Kafka (Aiven) - Mensaje enviado: " + mensaje);
    }
}
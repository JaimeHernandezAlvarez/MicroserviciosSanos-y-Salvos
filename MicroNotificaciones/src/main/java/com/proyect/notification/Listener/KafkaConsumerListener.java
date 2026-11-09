package com.proyect.notification.Listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener {
    @KafkaListener(topics = "notificaciones-stream", groupId = "notificaciones-group")
    public void consumirEvento(String mensaje) {
        System.out.println("📥 Kafka (Aiven) - Mensaje recibido: " + mensaje);
    }
}
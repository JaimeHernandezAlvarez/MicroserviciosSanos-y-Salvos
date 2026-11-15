package com.proyect.notification.Listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class KafkaConsumerListener {

    // Lista en memoria para guardar los últimos mensajes
    private final List<String> mensajesRecientes = Collections.synchronizedList(new ArrayList<>());

    @KafkaListener(topics = "notificaciones-stream", groupId = "notificaciones-group")
    public void consumirEvento(String mensaje) {
        String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logMessage = "[" + hora + "] " + mensaje;
        
        System.out.println(" Kafka (Aiven) - Mensaje recibido: " + logMessage);
        
        // Guardamos el mensaje en la lista
        mensajesRecientes.add(logMessage);
        
        // Mantenemos solo los últimos 10 mensajes
        if (mensajesRecientes.size() > 10) {
            mensajesRecientes.remove(0);
        }
    }

    // Método para que el Controlador pueda obtener los mensajes
    public List<String> obtenerMensajesRecientes() {
        return mensajesRecientes;
    }
}
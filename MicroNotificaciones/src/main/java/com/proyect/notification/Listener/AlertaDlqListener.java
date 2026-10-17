package com.proyect.notification.Listener;

import com.proyect.notification.Config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AlertaDlqListener {

    private static final Logger log = LoggerFactory.getLogger(AlertaDlqListener.class);

    //Usamos el containerFactory exclusivo de la DLQ
    @RabbitListener(queues = RabbitMQConfig.DLQ_QUEUE, containerFactory = "dlqContainerFactory")
    public void alertarMensajeFallido(byte[] body) {
        String contenido = new String(body);
        log.error("ALERTA: Mensaje fallido en DLQ");
        log.error("   Contenido: {}", contenido);
        log.error("   Accion requerida: revisar MicroNotificaciones");
        log.error("   Timestamp: {}", LocalDateTime.now());
    }
}
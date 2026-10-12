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

    @RabbitListener(queues = RabbitMQConfig.DLQ_QUEUE)
    public void alertarMensajeFallido(String mensaje) {
        log.error("ALERTA: Mensaje fallido en DLQ");
        log.error("   Contenido: {}", mensaje);
        log.error("   Accion requerida: revisar MicroNotificaciones");
        log.error("   Timestamp: {}", LocalDateTime.now());
    }
}
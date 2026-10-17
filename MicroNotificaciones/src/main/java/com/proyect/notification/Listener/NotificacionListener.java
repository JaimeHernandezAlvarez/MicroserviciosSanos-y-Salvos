package com.proyect.notification.Listener;

import com.proyect.notification.Client.UsuarioClient;
import com.proyect.notification.Config.RabbitMQConfig;
import com.proyect.notification.Dto.MascotaEncontradaEvent;
import com.proyect.notification.Dto.UsuarioResponseDTO;
import com.proyect.notification.Model.Notificacion;
import com.proyect.notification.Repository.NotificacionRepository;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class NotificacionListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionListener.class);

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICACIONES)
    public void procesarMascotaEncontrada(
            MascotaEncontradaEvent evento,
            Message message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("Evento recibido: Mascota {} encontrada por {}",
                 evento.getMascotaId(), evento.getFounderId());

        try {
            // 1. Llamada REST SINCRONA a MicroUsuario
            UsuarioResponseDTO dueno = usuarioClient.obtenerUsuario(evento.getOwnerId());

            if (dueno == null) {
                log.error("No se encontro el dueno con ID {}", evento.getOwnerId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            log.info("Datos del dueno: {} <{}>", dueno.getName(), dueno.getEmail());

            // 2. Guardar notificacion (ASINCRONO)
            Notificacion notificacion = new Notificacion();
            notificacion.setUserId(evento.getOwnerId());
            notificacion.setTitle("Tu mascota fue encontrada");
            notificacion.setMessage("La mascota con ID " + evento.getMascotaId() + " fue reportada como encontrada.");
            notificacion.setType("MASCOTA_ENCONTRADA");
            notificacion.setRelatedEntityId(evento.getMascotaId());
            notificacion.setRead(false);
            notificacion.setCreatedAt(LocalDateTime.now());

            notificacionRepository.save(notificacion);

            // 3. ACK EXPLICITO
            channel.basicAck(deliveryTag, false);
            log.info("Notificacion guardada y ACK enviado - DeliveryTag: {}", deliveryTag);

        } catch (Exception e) {
            log.error("Error procesando evento: {}", e.getMessage(), e);

            // NACK: rechazar el mensaje, va a DLQ
            channel.basicNack(deliveryTag, false, false);
            log.warn("Mensaje enviado a DLQ - DeliveryTag: {}", deliveryTag);
        }
    }
}
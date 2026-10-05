package com.proyect.notification.Listener;

import com.proyect.notification.Client.UsuarioClient;
import com.proyect.notification.Config.RabbitMQConfig;
import com.proyect.notification.Dto.MascotaEncontradaEvent;
import com.proyect.notification.Dto.UsuarioResponseDTO;
import com.proyect.notification.Model.Notificacion;
import com.proyect.notification.Repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificacionListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionListener.class);

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICACIONES)
    public void procesarMascotaEncontrada(MascotaEncontradaEvent evento) {
        log.info("Evento recibido: Mascota {} encontrada por {}",
                 evento.getMascotaId(), evento.getFounderId());

        UsuarioResponseDTO dueno = usuarioClient.obtenerUsuario(evento.getOwnerId());

        if (dueno == null) {
            log.error("No se encontró el dueño con ID {}", evento.getOwnerId());
            return;
        }

        log.info("Datos del dueño: {} <{}>", dueno.getName(), dueno.getEmail());

        Notificacion notificacion = new Notificacion();
        notificacion.setUserId(evento.getOwnerId());
        notificacion.setTitle("¡Tu mascota fue encontrada!");
        notificacion.setMessage("La mascota con ID " + evento.getMascotaId() + " fue reportada como encontrada.");
        notificacion.setType("MASCOTA_ENCONTRADA");
        notificacion.setRelatedEntityId(evento.getMascotaId());
        notificacion.setRead(false);
        notificacion.setCreatedAt(LocalDateTime.now());

        notificacionRepository.save(notificacion);

        log.info("Notificación guardada correctamente para {}", dueno.getEmail());
    }
}
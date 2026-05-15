package com.proyect.notification.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.proyect.notification.Controller.NotificacionController;
import com.proyect.notification.Model.Notificacion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class NotificacionModelAssembler implements RepresentationModelAssembler<Notificacion, EntityModel<Notificacion>> {

    @SuppressWarnings("null")
    @Override
    public EntityModel<Notificacion> toModel(Notificacion notificacion) {
        // Creamos el modelo base que envuelve la entidad
        EntityModel<Notificacion> notificacionModel = EntityModel.of(notificacion);

        // Enlace al propio recurso individual (self)
        notificacionModel.add(linkTo(methodOn(NotificacionController.class)
                .obtenerPorId(notificacion.getId())).withSelfRel());

        // Enlace para marcar esta notificación específica como leída
        notificacionModel.add(linkTo(methodOn(NotificacionController.class)
                .marcarComoLeida(notificacion.getId())).withRel("marcar-como-leida"));

        // Enlace para ver todo el historial del usuario al que pertenece la notificación
        notificacionModel.add(linkTo(methodOn(NotificacionController.class)
                .listarPorUsuario(notificacion.getUserId())).withRel("historial-usuario"));

        return notificacionModel;
    }
}
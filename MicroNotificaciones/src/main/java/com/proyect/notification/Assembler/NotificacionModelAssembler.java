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
        EntityModel<Notificacion> notificacionModel = EntityModel.of(notificacion);

        notificacionModel.add(linkTo(methodOn(NotificacionController.class)
                .obtenerPorId(notificacion.getId())).withSelfRel());
        notificacionModel.add(linkTo(methodOn(NotificacionController.class)
                .marcarComoLeida(notificacion.getId())).withRel("marcar-como-leida"));
        notificacionModel.add(linkTo(methodOn(NotificacionController.class)
                .listarPorUsuario(notificacion.getUserId())).withRel("historial-usuario"));

        return notificacionModel;
    }
}
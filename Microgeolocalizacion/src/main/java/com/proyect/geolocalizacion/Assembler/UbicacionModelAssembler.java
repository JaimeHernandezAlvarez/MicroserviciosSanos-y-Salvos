package com.proyect.geolocalizacion.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.proyect.geolocalizacion.controller.UbicacionController;
import com.proyect.geolocalizacion.model.Ubicacion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class UbicacionModelAssembler implements RepresentationModelAssembler<Ubicacion, EntityModel<Ubicacion>> {

    @SuppressWarnings("null")
    @Override
    public EntityModel<Ubicacion> toModel(Ubicacion ubicacion) {
        EntityModel<Ubicacion> ubicacionModel = EntityModel.of(ubicacion);

        // Enlace principal (self) hacia el GET /{id}
        ubicacionModel.add(linkTo(methodOn(UbicacionController.class)
                .obtenerPorId(ubicacion.getId())).withSelfRel());

        // Enlace al historial de la mascota (pet)
        if (ubicacion.getReportId() != null) { 
            ubicacionModel.add(linkTo(methodOn(UbicacionController.class)
                    .obtenerPorMascota(ubicacion.getReportId())).withRel("historial-mascota"));
        }

        return ubicacionModel;
    }
}
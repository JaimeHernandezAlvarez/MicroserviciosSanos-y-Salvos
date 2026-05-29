package com.proyect.pet.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.proyect.pet.Controller.MascotaController;
import com.proyect.pet.Model.Mascota;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("null")
public class MascotaModelAssembler implements RepresentationModelAssembler<Mascota, EntityModel<Mascota>> {
    @Override
    public EntityModel<Mascota> toModel(@SuppressWarnings("null") Mascota mascota) {
        @SuppressWarnings("null")
        EntityModel<Mascota> mascotaModel = EntityModel.of(mascota);
        mascotaModel.add(linkTo(methodOn(MascotaController.class).obtenerPorId(mascota.getId())).withSelfRel());
        mascotaModel.add(linkTo(methodOn(MascotaController.class).listarTodas()).withRel("todas-las-mascotas"));
        mascotaModel.add(linkTo(methodOn(MascotaController.class).actualizar(mascota.getId(), new Mascota()))
                .withRel("actualizar-info"));
        mascotaModel.add(linkTo(methodOn(MascotaController.class).cambiarEstado(mascota.getId(), "FOUND", null))
                .withRel("cambiar-estado"));

        return mascotaModel;
    }
}
package com.proyect.user.Assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.proyect.user.Controller.AuthController;
import com.proyect.user.Model.Usuario;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@SuppressWarnings("null")
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(AuthController.class).login(null)).withRel("login"),
                linkTo(methodOn(AuthController.class).refreshToken(null)).withRel("refresh")
        );
    }
}
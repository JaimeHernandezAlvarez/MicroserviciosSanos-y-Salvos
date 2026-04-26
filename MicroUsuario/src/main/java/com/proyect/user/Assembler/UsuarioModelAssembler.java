package com.proyect.user.Assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.proyect.user.Controller.UsuarioController;
import com.proyect.user.Model.Usuario;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@SuppressWarnings("null")
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        // Aquí centralizamos TODOS los links que un usuario debería tener
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withRel("todos-los-usuarios"),
                linkTo(methodOn(UsuarioController.class).actualizarUsuario(usuario.getId(), null)).withRel("actualizar-usuario"),
                linkTo(methodOn(UsuarioController.class).eliminarUsuario(usuario.getId())).withRel("eliminar-usuario")
        );
    }
}
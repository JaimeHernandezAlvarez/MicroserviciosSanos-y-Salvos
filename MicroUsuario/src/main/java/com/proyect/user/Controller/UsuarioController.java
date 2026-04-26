package com.proyect.user.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyect.user.Assembler.UsuarioModelAssembler;
import com.proyect.user.Model.Usuario;
import com.proyect.user.Service.UsuarioService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios")
@SuppressWarnings("null")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Inyectamos nuestro nuevo Assembler
    @Autowired
    private UsuarioModelAssembler assembler;

    // GET /api/usuarios
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> obtenerUsuarios() {
        List<EntityModel<Usuario>> usuariosModel = usuarioService.findAll().stream()
                .map(assembler::toModel) // <- Usamos el assembler aquí
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Usuario>> collectionModel = CollectionModel.of(usuariosModel,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@PathVariable String id) {
        return usuarioService.findById(id)
                .map(assembler::toModel) // <- Usamos el assembler aquí
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/usuarios/register
    @PostMapping("/register")
    public ResponseEntity<EntityModel<Usuario>> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(nuevoUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // POST /api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<EntityModel<Usuario>> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        Usuario usuario = usuarioService.login(email, password);
        
        if (usuario != null) {
            return ResponseEntity.ok(assembler.toModel(usuario));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // PUT /api/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(@PathVariable String id, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.update(id, usuario);
        if (usuarioActualizado != null) {
            return ResponseEntity.ok(assembler.toModel(usuarioActualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        if (usuarioService.delete(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
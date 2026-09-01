package com.proyect.user.Controller;

import com.proyect.user.Assembler.UsuarioModelAssembler;
import com.proyect.user.DTO.LoginRequestDTO;
import com.proyect.user.DTO.AuthResponseDTO;
import com.proyect.user.DTO.UsuarioResponseDTO;
import com.proyect.user.Model.Usuario;
import com.proyect.user.Security.JwtUtil;
import com.proyect.user.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private UsuarioModelAssembler assembler;

    @Autowired
    private JwtUtil jwtUtil;

    // ==================== NUEVO: Injectar AuthenticationManager ====================
    @Autowired
    private AuthenticationManager authenticationManager;

    // ==================== MÉTODOS EXISTENTES (SE MANTIENEN) ====================
    
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> obtenerUsuarios() {
        List<EntityModel<Usuario>> usuariosModel = usuarioService.findAll().stream()
                .map(assembler::toModel) 
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Usuario>> collectionModel = CollectionModel.of(usuariosModel,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@PathVariable String id) {
        return usuarioService.findById(id)
                .map(assembler::toModel) 
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<EntityModel<Usuario>> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(nuevoUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // ==================== MÉTODO LOGIN MEJORADO ====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // 1. Autenticar al usuario usando Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Obtener el usuario
            Usuario usuario = usuarioService.findByEmail(loginRequest.getEmail());
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado"));
            }

            // 3. Generar tokens
            String token = jwtUtil.generarToken(usuario);
            String refreshToken = jwtUtil.generarRefreshToken(usuario);

            // 4. Construir respuesta usando DTO
            UsuarioResponseDTO userDTO = mapToDTO(usuario);
            AuthResponseDTO response = new AuthResponseDTO(userDTO, token, refreshToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales incorrectas", "message", e.getMessage()));
        }
    }

    // ==================== NUEVO: Endpoint de Refresh Token ====================
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Refresh token requerido"));
        }

        try {
            // 1. Validar refresh token
            if (!jwtUtil.validarToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token inválido"));
            }

            // 2. Extraer email del refresh token
            String email = jwtUtil.extraerEmail(refreshToken);
            
            // 3. Buscar usuario
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado"));
            }

            // 4. Generar nuevos tokens
            String newToken = jwtUtil.generarToken(usuario);
            String newRefreshToken = jwtUtil.generarRefreshToken(usuario);

            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("refreshToken", newRefreshToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Error al refrescar token", "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(@PathVariable String id, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.update(id, usuario);
        if (usuarioActualizado != null) {
            return ResponseEntity.ok(assembler.toModel(usuarioActualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        if (usuarioService.delete(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== MÉTODO AUXILIAR: Map a DTO ====================
    private UsuarioResponseDTO mapToDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setName(usuario.getName());
        dto.setPhone(usuario.getPhone());
        dto.setRole(usuario.getRole().name());
        dto.setActive(usuario.getActive());
        dto.setPetsIds(usuario.getPetsIds() != null ? usuario.getPetsIds() : new ArrayList<>());
        return dto;
    }
}
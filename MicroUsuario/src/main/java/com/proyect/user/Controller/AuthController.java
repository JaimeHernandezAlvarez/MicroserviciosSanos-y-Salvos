package com.proyect.user.Controller;

import com.proyect.user.DTO.AuthResponseDTO;
import com.proyect.user.DTO.LoginRequestDTO;
import com.proyect.user.DTO.RefreshTokenRequestDTO;
import com.proyect.user.DTO.UsuarioResponseDTO;
import com.proyect.user.Model.Usuario;
import com.proyect.user.Security.JwtUtil;
import com.proyect.user.Service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@SuppressWarnings("null")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    // ==================== AUTENTICACIÓN ====================

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y genera tokens JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Usuario usuario = usuarioService.findByEmail(loginRequest.getEmail());
            if (usuario == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Usuario no encontrado"
                ));
            }

            String token = jwtUtil.generarToken(usuario);
            String refreshToken = jwtUtil.generarRefreshToken(usuario);

            UsuarioResponseDTO userDTO = mapToDTO(usuario);
            AuthResponseDTO response = new AuthResponseDTO(userDTO, token, refreshToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Credenciales incorrectas",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Obtiene un nuevo token usando el refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido")
    })
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Refresh token requerido"
            ));
        }

        try {
            if (!jwtUtil.validarToken(refreshToken)) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Refresh token inválido"
                ));
            }

            String email = jwtUtil.extraerEmail(refreshToken);
            
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Usuario no encontrado"
                ));
            }

            String newToken = jwtUtil.generarToken(usuario);
            String newRefreshToken = jwtUtil.generarRefreshToken(usuario);

            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("refreshToken", newRefreshToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Error al refrescar token",
                "message", e.getMessage()
            ));
        }
    }

    // ==================== CRUD DE USUARIOS ====================

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Listar usuarios", description = "Retorna todos los usuarios. Solo administradores.")
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario por su ID")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable String id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> actualizar(@PathVariable String id, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.update(id, usuario);
        if (usuarioActualizado != null) {
            return ResponseEntity.ok(usuarioActualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema. Solo administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para eliminar usuarios")
    })
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (usuarioService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== MÉTODO AUXILIAR ====================

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
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Ruta base para autenticación
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

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
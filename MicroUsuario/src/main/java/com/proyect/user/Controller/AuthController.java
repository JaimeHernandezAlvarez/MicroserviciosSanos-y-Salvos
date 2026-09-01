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
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Endpoint para iniciar sesión
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y genera tokens JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // 1. Autenticar al usuario
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
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Usuario no encontrado"
                ));
            }

            // 3. Generar tokens
            String token = jwtUtil.generarToken(usuario);
            String refreshToken = jwtUtil.generarRefreshToken(usuario);

            // 4. Mapear a DTO
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

    /**
     * Endpoint para refrescar el token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Obtiene un nuevo token usando el refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido")
    })
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        // 1. Validar el refresh token
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Refresh token requerido"
            ));
        }

        try {
            // 2. Verificar que el refresh token sea válido
            if (!jwtUtil.validarToken(refreshToken)) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Refresh token inválido"
                ));
            }

            // 3. Extraer el email del refresh token
            String email = jwtUtil.extraerEmail(refreshToken);
            
            // 4. Buscar el usuario
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Usuario no encontrado"
                ));
            }

            // 5. Generar nuevos tokens
            String newToken = jwtUtil.generarToken(usuario);
            String newRefreshToken = jwtUtil.generarRefreshToken(usuario);

            // 6. Construir respuesta
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

    /**
     * Mapea un Usuario a UsuarioResponseDTO
     */
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
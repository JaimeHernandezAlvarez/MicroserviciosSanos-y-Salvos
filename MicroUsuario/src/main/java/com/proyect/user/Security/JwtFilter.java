package com.proyect.user.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Rutas que no requieren autenticación.
     */
    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/usuarios/register",
        "/v3/api-docs",
        "/swagger-ui",
        "/actuator/health"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Log de la petición
        logger.debug("Procesando petición: {} {}", method, requestPath);

        // 1. Verificar si la ruta es pública
        if (isPublicPath(requestPath)) {
            logger.debug("Ruta pública, continuando sin autenticación");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Obtener el header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Token no encontrado en la petición: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token no proporcionado\", \"status\": 401}");
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 3. Validar el token
            if (jwtUtil.validarToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 4. Extraer información del token
                String email = jwtUtil.extraerEmail(token);
                String rol = jwtUtil.extraerRol(token);
                Long id = jwtUtil.extraerId(token);

                logger.info("Token válido para usuario: {} con rol: {} y id: {}", email, rol, id);

                // 5. Crear la autenticación
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol);
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Guardar en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.warn("Token inválido o ya autenticado para: {}", requestPath);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token inválido o expirado\", \"status\": 401}");
                return;
            }

        } catch (Exception e) {
            logger.error("Error al procesar el token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\", \"status\": 401}");
            return;
        }

        // 7. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Verifica si la ruta es pública.
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * No aplicar el filtro a rutas públicas por defecto.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return isPublicPath(path);
    }
}
package com.proyect.user.Security;

import com.proyect.user.Model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:SanosYSalvos_ClaveSecreta_SuperSegura_Para_JWT_2026_Backend}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private long expirationTime;

    @Value("${jwt.refresh-expiration:604800000}") // 7 días en milisegundos
    private long refreshExpirationTime;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Genera un token JWT de acceso para el usuario.
     * Incluye email, rol e ID del usuario en los claims.
     * 
     * @param usuario Usuario autenticado
     * @return Token JWT firmado
     */
    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRole().name());
        claims.put("id", usuario.getId()); // ← ID como String
        claims.put("email", usuario.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Genera un refresh token con mayor duración.
     * 
     * @param usuario Usuario autenticado
     * @return Refresh token JWT firmado
     */
    public String generarRefreshToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae todos los claims del token.
     * 
     * @param token Token JWT
     * @return Claims del token
     * @throws JwtException Si el token es inválido o expirado
     */
    private Claims extraerClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.error("Token inválido: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el email del token.
     * 
     * @param token Token JWT
     * @return Email del usuario
     */
    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    /**
     * Obtiene el rol del token.
     * 
     * @param token Token JWT
     * @return Rol del usuario
     */
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    /**
     * Obtiene el ID del usuario del token.
     * 
     * @param token Token JWT
     * @return ID del usuario (String)
     */
    public String extraerId(String token) {
        return extraerClaims(token).get("id", String.class); // ← String.class
    }

    /**
     * Verifica si el token ha expirado.
     * 
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = extraerClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Valida el token comprobando la firma y la expiración.
     * 
     * @param token Token JWT
     * @return true si el token es válido, false en caso contrario
     */
    public Boolean validarToken(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida que el token pertenezca al usuario específico.
     * 
     * @param token Token JWT
     * @param usuario Usuario a validar
     * @return true si el token pertenece al usuario y no ha expirado
     */
    public Boolean validarTokenParaUsuario(String token, Usuario usuario) {
        String email = extraerEmail(token);
        return (email.equals(usuario.getEmail()) && !isTokenExpired(token));
    }
}
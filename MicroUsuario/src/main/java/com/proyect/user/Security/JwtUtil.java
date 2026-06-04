package com.proyect.user.Security;

import com.proyect.user.Model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Llave secreta para firmar el token (¡En producción esto debe ir en tu .env!)
    // Debe ser un string largo y seguro.
    private final String SECRET_KEY = "SanosYSalvos_ClaveSecreta_SuperSegura_Para_JWT_2026_Backend";
    
    // Tiempo de expiración del token: 1 día (en milisegundos)
    private final long EXPIRATION_TIME = 86400000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        // Guardamos el rol y el ID dentro del token para que los otros microservicios lo puedan leer
        claims.put("rol", usuario.getRole().name());
        claims.put("id", usuario.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail()) // El "sujeto" del token es el email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // ... código anterior (SECRET_KEY, getSigningKey, generarToken) ...

    // 1. Extraer todos los datos (Claims) del token
    private io.jsonwebtoken.Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 2. Obtener el email del token
    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    // 3. Obtener el rol del token
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    // 4. Validar si el token es legítimo y no ha expirado
    public boolean validarToken(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (Exception e) {
            return false; // Si cae aquí, el token fue alterado, expiró o es inválido
        }
    }
}
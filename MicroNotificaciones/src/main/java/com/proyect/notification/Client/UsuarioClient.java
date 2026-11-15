package com.proyect.notification.Client;

import com.proyect.notification.Dto.UsuarioResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Value("${micro.usuario.url}") String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    public UsuarioResponseDTO obtenerUsuario(String id) {
        return webClient.get()
            .uri("/api/usuarios/{id}/internal", id)
            .retrieve()
            .bodyToMono(UsuarioResponseDTO.class)
            .block();
    }
}
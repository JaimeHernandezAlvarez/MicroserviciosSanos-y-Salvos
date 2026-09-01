package com.proyect.user.DTO;

public class AuthResponseDTO {
    private UsuarioResponseDTO usuario;
    private String token;
    private String refreshToken;

    // Constructores
    public AuthResponseDTO() {}

    public AuthResponseDTO(UsuarioResponseDTO usuario, String token, String refreshToken) {
        this.usuario = usuario;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    // Getters y Setters
    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
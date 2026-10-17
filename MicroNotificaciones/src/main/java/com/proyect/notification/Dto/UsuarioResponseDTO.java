package com.proyect.notification.Dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private String id;
    private String email;
    private String name;
    private String phone;
    private String role;
    private Boolean active;
}
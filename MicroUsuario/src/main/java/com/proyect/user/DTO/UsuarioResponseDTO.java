package com.proyect.user.DTO;

import java.util.List;

public class UsuarioResponseDTO {
    private String id;  // ← CAMBIADO a String
    private String email;
    private String name;
    private String phone;
    private String role;
    private Boolean active;
    private List<String> petsIds;  // ← CAMBIADO a List<String>

    // Constructores
    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(String id, String email, String name, String phone, 
                              String role, Boolean active, List<String> petsIds) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.active = active;
        this.petsIds = petsIds;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<String> getPetsIds() {
        return petsIds;
    }

    public void setPetsIds(List<String> petsIds) {
        this.petsIds = petsIds;
    }
}
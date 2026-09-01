package com.proyect.user.Model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String password;
    private String name;
    private String phone;
    private Rol role = Rol.ROLE_USER;
    private List<String> petsIds;
    private Boolean active = true;  // ✅ CAMBIADO de boolean a Boolean
    private String refreshToken;

    // ✅ GETTER Y SETTER MANUALES (por si Lombok no funciona)
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Rol getRole() {
        return role;
    }

    public void setRole(Rol role) {
        this.role = role;
    }

    public List<String> getPetsIds() {
        return petsIds;
    }

    public void setPetsIds(List<String> petsIds) {
        this.petsIds = petsIds;
    }

    // ✅ Getter y Setter para active (usa Boolean)
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
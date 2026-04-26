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
    private String role; 
    private List<String> petsIds;
    private boolean active;
    private String refreshToken;
}
package com.proyect.pet.Config; // Recuerda cambiar "tu_microservicio" por el nombre que corresponda

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
        registry.addMapping("/**") // Habilita CORS para todas las rutas del microservicio
                // Aquí colocamos los puertos típicos de React. Puedes agregar más si usan otros.
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                // allowCredentials es VITAL si en el futuro envían tokens JWT en cookies o headers de autorización
                .allowCredentials(true) 
                .maxAge(3600); // Guarda en caché la validación de CORS por 1 hora para optimizar el frontend
    }
}
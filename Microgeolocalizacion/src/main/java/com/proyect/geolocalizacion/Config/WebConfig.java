package com.proyect.geolocalizacion.Config; // Recuerda cambiar "tu_microservicio" por el nombre que corresponda

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
        registry.addMapping("/**") 
                //puertos
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                //credenciales
                .allowCredentials(true) 
                .maxAge(3600); 
    }
}
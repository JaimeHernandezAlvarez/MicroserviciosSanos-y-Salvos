package com.proyect.notification.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Notificaciones - Fullstack 3")
                        .version("1.0")
                        .description("Documentación interactiva de la API para el sistema de alertas (tipo campanita). Permite gestionar notificaciones de matches, alertas de zona y mensajes del sistema. Incluye soporte HATEOAS."));
    }
}
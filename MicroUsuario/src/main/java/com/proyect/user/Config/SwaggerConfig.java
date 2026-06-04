package com.proyect.user.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                // Le dice a Swagger que todos los endpoints pueden usar esta seguridad
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Define cómo funciona el esquema de seguridad (Bearer Token)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("API de Usuarios - Fullstack 3")
                        .version("1.0")
                        .description("Documentación interactiva de la API para la gestión de usuarios. Incluye soporte HATEOAS."));
    }
}
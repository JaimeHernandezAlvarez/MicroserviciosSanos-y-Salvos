package com.proyect.match;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableMongoRepositories
public class MatchApplication {
    public static void main(String[] args) {
        // Cargar variables de entorno desde .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        // Establecer propiedades del sistema
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        
        SpringApplication.run(MatchApplication.class, args);
    }
}
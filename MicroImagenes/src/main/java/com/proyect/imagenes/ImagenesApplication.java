package com.proyect.imagenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableMongoRepositories
public class ImagenesApplication {
    public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("IMG_URI", dotenv.get("IMG_URI") );
        SpringApplication.run(ImagenesApplication.class, args);
    }
}
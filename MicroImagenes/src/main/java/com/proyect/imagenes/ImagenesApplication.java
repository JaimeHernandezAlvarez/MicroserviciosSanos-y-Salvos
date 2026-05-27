package com.proyect.imagenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ImagenesApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String imgUri = dotenv.get("IMG_URI");
        if (imgUri != null) {
            System.setProperty("IMG_URI", imgUri);
        }
        SpringApplication.run(ImagenesApplication.class, args);
    }
}
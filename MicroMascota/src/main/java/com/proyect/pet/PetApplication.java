package com.proyect.pet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class PetApplication {
public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    System.setProperty("PET_URI", dotenv.get("PET_URI"));
		SpringApplication.run(PetApplication.class, args);
	}

}

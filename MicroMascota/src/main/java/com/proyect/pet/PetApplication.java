package com.proyect.pet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class PetApplication {
public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String petUri = dotenv.get("PET_URI");
    if (petUri != null) {
        System.setProperty("PET_URI", petUri);
    }
		SpringApplication.run(PetApplication.class, args);
	}

}

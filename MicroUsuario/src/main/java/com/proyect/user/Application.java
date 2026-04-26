package com.proyect.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class Application {
public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    System.setProperty("USER_URI", dotenv.get("USER_URI"));
		SpringApplication.run(Application.class, args);
	}

}

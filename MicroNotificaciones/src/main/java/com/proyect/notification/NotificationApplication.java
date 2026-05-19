package com.proyect.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class NotificationApplication {
public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    System.setProperty("NOT_URI", dotenv.get("NOT_URI"));
		SpringApplication.run(NotificationApplication.class, args);
	}
}

package com.proyect.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class NotificationApplication {
public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String notUri = dotenv.get("NOT_URI");
    if (notUri != null) {
        System.setProperty("NOT_URI", notUri);
    }
		SpringApplication.run(NotificationApplication.class, args);
	}
}

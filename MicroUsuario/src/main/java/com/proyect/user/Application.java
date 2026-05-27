package com.proyect.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class Application {
public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String userUri = dotenv.get("USER_URI");
    if (userUri != null) {
        System.setProperty("USER_URI", userUri);
    }
		SpringApplication.run(Application.class, args);
	}
}

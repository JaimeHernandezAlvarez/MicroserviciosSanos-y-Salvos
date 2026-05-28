package com.proyect.geolocalizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class GeolocalizacionApplication {
	public static void main(String[] args) {
	Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String geoUri = dotenv.get("GEO_DB_URI");
    if (geoUri != null) {
        System.setProperty("GEO_DB_URI", geoUri);
    }
		SpringApplication.run(GeolocalizacionApplication.class, args);
	}
}

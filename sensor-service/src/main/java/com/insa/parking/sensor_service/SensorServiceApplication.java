package com.insa.parking.sensor_service; // Vérifiez que le package correspond au votre

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling // <--- TRÈS IMPORTANT : Autorise les tâches automatiques
public class SensorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorServiceApplication.class, args);
	}

	// On crée un outil pour envoyer des requêtes HTTP aux autres services
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
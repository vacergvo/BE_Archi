package com.insa.parking.sensor_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Random;

@Component
public class SensorSimulator {

    @Autowired
    private RestTemplate restTemplate;

    private final Random random = new Random();

    @Scheduled(fixedRate = 10000)
    public void simulateSensorActivity() {
        
        //Random spot number
        int spotId = random.nextInt(8) + 1;

        //Random status
        String[] statusPossibles = {"Libre", "Occupé"};
        String nouveauStatus = statusPossibles[random.nextInt(statusPossibles.length)];

        System.out.println("⚡ CAPTEUR : Détection de changement sur la place " + spotId + " -> " + nouveauStatus);

        //Port 8082
        String url = "http://localhost:8082/api/spots/" + spotId + "/status";
        
        try {
            restTemplate.put(url, nouveauStatus);
            System.out.println("✅ Info envoyée au Parking Spot Service !");
        } catch (Exception e) {
            System.err.println("❌ Erreur : Impossible de joindre le Parking Spot Service. Est-il lancé ?");
        }
    }
}
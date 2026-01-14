package com.insa.parking.client_ui.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Controller // Notez @Controller et pas @RestController car on renvoie du HTML
public class ClientController {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/")
    public String dashboard(Model model, RestTemplate restTemplate) {
        // 1. Récupérer la liste des places depuis le Parking Spot Service (8082)
        String urlSpots = "http://localhost:8082/api/spots";
        List<Map<String, Object>> spots = restTemplate.getForObject(urlSpots, List.class);

        // 2. Récupérer les réservations (8083)
        String urlResa = "http://localhost:8083/api/reservations";
        List<Map<String, Object>> reservations = restTemplate.getForObject(urlResa, List.class);
        
        // 3. Envoyer les données à la page HTML
        model.addAttribute("spots", spots);
        model.addAttribute("reservations", reservations);

        return "dashboard"; // Cherchera le fichier dashboard.html
    }
}
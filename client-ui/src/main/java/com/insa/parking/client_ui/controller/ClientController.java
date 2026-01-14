package com.insa.parking.client_ui.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Controller
public class ClientController {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/")
    public String dashboard(Model model, RestTemplate restTemplate) {
        //Dump spot tab (port 8082)
        String urlSpots = "http://localhost:8082/api/spots";
        List<Map<String, Object>> spots = restTemplate.getForObject(urlSpots, List.class);

        //Dump reservation (port 8083)
        String urlResa = "http://localhost:8083/api/reservations";
        List<Map<String, Object>> reservations = restTemplate.getForObject(urlResa, List.class);
        
        //Send html
        model.addAttribute("spots", spots);
        model.addAttribute("reservations", reservations);

        return "dashboard";
    }
}
package com.insa.parking.entry_exit_service.controller;

import com.insa.parking.entry_exit_service.model.SessionParking;
import com.insa.parking.entry_exit_service.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
public class EntryExitController {

    @Autowired
    private SessionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public List<SessionParking> getAllSessions() {
        return repository.findAll();
    }

    // --- ENTRÉE ---
    @PostMapping("/enter")
    public ResponseEntity<?> enterParking(@RequestBody SessionParking session) {
        session.setHeureEntree(LocalDateTime.now());
        SessionParking savedSession = repository.save(session);
        System.out.println("🚗 ENTRÉE : " + session.getPlaqueImmat());

        // Dire au Parking Spot Service : "Occupé"
        try {
            String url = "http://localhost:8082/api/spots/" + session.getIdSpot() + "/status";
            restTemplate.put(url, "Occupé");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur communication Parking Service");
        }
        return ResponseEntity.ok(savedSession);
    }

    // --- SORTIE ---
    @PostMapping("/exit")
    public ResponseEntity<?> exitParking(@RequestBody Map<String, String> request) {
        String plaque = request.get("plaqueImmat");
        
        Optional<SessionParking> sessionOpt = repository.findAll().stream()
                .filter(s -> s.getPlaqueImmat().equals(plaque) && s.getHeureSortie() == null)
                .findFirst();

        if (sessionOpt.isEmpty()) return ResponseEntity.badRequest().body("Pas de session trouvée !");

        SessionParking session = sessionOpt.get();
        session.setHeureSortie(LocalDateTime.now());

        // Calcul du prix (0.10€ la seconde pour le test)
        long durationSeconds = Duration.between(session.getHeureEntree(), session.getHeureSortie()).getSeconds();
        session.setPrixTotal(BigDecimal.valueOf(durationSeconds * 0.10));

        repository.save(session);
        System.out.println("👋 SORTIE : " + plaque + " | Prix : " + session.getPrixTotal() + "€");

        // Dire au Parking Spot Service : "Libre"
        try {
            String url = "http://localhost:8082/api/spots/" + session.getIdSpot() + "/status";
            restTemplate.put(url, "Libre");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur communication Parking Service");
        }

        return ResponseEntity.ok(session);
    }
}
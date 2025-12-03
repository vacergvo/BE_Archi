package com.insa.parking.parking_spot_service.controller;

import com.insa.parking.parking_spot_service.model.ParkingSpot;
import com.insa.parking.parking_spot_service.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots") // L'adresse de base
public class ParkingSpotController {

    @Autowired
    private ParkingSpotRepository repository;

    // URL: GET http://localhost:8082/api/spots
    @GetMapping
    public List<ParkingSpot> getAllSpots() {
        return repository.findAll();
    }
    
    // URL: PUT http://localhost:8082/api/spots/{id}/status
    // Sert au Sensor Service pour mettre à jour une place
    @PutMapping("/{id}/status")
    public ResponseEntity<ParkingSpot> updateStatus(@PathVariable Integer id, @RequestBody String newStatus) {
        return repository.findById(id)
            .map(spot -> {
                // On nettoie le string (enlève les guillemets éventuels)
                String statusClean = newStatus.replace("\"", "").trim();
                spot.setStatus(ParkingSpot.SpotStatus.valueOf(statusClean));
                return ResponseEntity.ok(repository.save(spot));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
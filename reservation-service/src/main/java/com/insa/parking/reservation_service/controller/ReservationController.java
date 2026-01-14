package com.insa.parking.reservation_service.controller;

import com.insa.parking.reservation_service.model.Reservation;
import com.insa.parking.reservation_service.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {
        
        Integer spotId = reservation.getIdSpot();
        String parkingUrl = "http://localhost:8082/api/spots/" + spotId;

        try {
            Map<String, Object> spot = restTemplate.getForObject(parkingUrl, Map.class);
            
            String currentStatus = (String) spot.get("status");
            
            if (!"Libre".equals(currentStatus)) {
                return ResponseEntity.badRequest().body("❌ Erreur : La place n'est pas libre (Status actuel : " + currentStatus + ")");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Erreur : Impossible de contacter le Parking Spot Service ou place inexistante.");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.Confirmée);
        Reservation savedReservation = repository.save(reservation);

        try {
            restTemplate.put(parkingUrl + "/status", "Réservé");
            System.out.println("✅ Place " + spotId + " marquée comme Réservée.");
        } catch (Exception e) {
            System.err.println("⚠️ Réservation enregistrée mais erreur lors de la mise à jour du status parking.");
        }

        return ResponseEntity.ok(savedReservation);
    }
}
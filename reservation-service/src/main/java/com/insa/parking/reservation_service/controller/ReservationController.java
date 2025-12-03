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

    // Récupérer toutes les réservations
    @GetMapping
    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    // CRÉER UNE RÉSERVATION INTELLIGENTE
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {
        
        Integer spotId = reservation.getIdSpot();
        String parkingUrl = "http://localhost:8082/api/spots/" + spotId;

        // 1. Vérifier si la place existe et est Libre
        try {
            // On demande au Parking Spot Service l'état de la place (il nous renvoie un objet JSON/Map)
            Map<String, Object> spot = restTemplate.getForObject(parkingUrl, Map.class);
            
            String currentStatus = (String) spot.get("status");
            
            if (!"Libre".equals(currentStatus)) {
                return ResponseEntity.badRequest().body("❌ Erreur : La place n'est pas libre (Status actuel : " + currentStatus + ")");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Erreur : Impossible de contacter le Parking Spot Service ou place inexistante.");
        }

        // 2. Si c'est Libre, on enregistre la réservation dans notre base locale
        reservation.setStatus(Reservation.ReservationStatus.Confirmée);
        Reservation savedReservation = repository.save(reservation);

        // 3. On appelle le Parking Spot Service pour changer le status en "Réservé"
        try {
            restTemplate.put(parkingUrl + "/status", "Réservé");
            System.out.println("✅ Place " + spotId + " marquée comme Réservée.");
        } catch (Exception e) {
            System.err.println("⚠️ Réservation enregistrée mais erreur lors de la mise à jour du status parking.");
        }

        return ResponseEntity.ok(savedReservation);
    }
}
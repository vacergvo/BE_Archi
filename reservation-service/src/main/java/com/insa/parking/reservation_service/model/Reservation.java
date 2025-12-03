package com.insa.parking.reservation_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVATION")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Integer id;

    // On stocke juste l'ID de la place (Microservices = pas de liaison directe d'objet entre services)
    @Column(name = "id_spot", nullable = false)
    private Integer idSpot;

    @Column(name = "plaque_immat", nullable = false)
    private String plaqueImmat;

    @Column(name = "heure_debut_res", nullable = false)
    private LocalDateTime heureDebut;

    @Column(name = "heure_fin_res", nullable = false)
    private LocalDateTime heureFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    public enum ReservationStatus {
        Confirmée, Annulée, Terminée
    }

    // --- GETTERS & SETTERS (Générez-les via Clic-droit > Source > Generate...) ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdSpot() { return idSpot; }
    public void setIdSpot(Integer idSpot) { this.idSpot = idSpot; }
    public String getPlaqueImmat() { return plaqueImmat; }
    public void setPlaqueImmat(String plaqueImmat) { this.plaqueImmat = plaqueImmat; }
    public LocalDateTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalDateTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalDateTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalDateTime heureFin) { this.heureFin = heureFin; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
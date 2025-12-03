package com.insa.parking.entry_exit_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "SESSION_PARKING")
public class SessionParking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Long id;

    @Column(name = "id_spot")
    private Integer idSpot;

    @Column(name = "plaque_immat", nullable = false)
    private String plaqueImmat;

    @Column(name = "heure_entree", nullable = false)
    private LocalDateTime heureEntree;

    @Column(name = "heure_sortie")
    private LocalDateTime heureSortie;

    @Column(name = "prix_total")
    private BigDecimal prixTotal;

    // ==========================================================
    //    VOICI LES MÉTHODES QUI MANQUAIENT (GETTERS & SETTERS)
    // ==========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdSpot() {
        return idSpot;
    }

    public void setIdSpot(Integer idSpot) {
        this.idSpot = idSpot;
    }

    public String getPlaqueImmat() {
        return plaqueImmat;
    }

    public void setPlaqueImmat(String plaqueImmat) {
        this.plaqueImmat = plaqueImmat;
    }

    public LocalDateTime getHeureEntree() {
        return heureEntree;
    }

    // C'EST CELLE-CI QUI BLOQUAIT VOTRE CODE :
    public void setHeureEntree(LocalDateTime heureEntree) {
        this.heureEntree = heureEntree;
    }

    public LocalDateTime getHeureSortie() {
        return heureSortie;
    }

    public void setHeureSortie(LocalDateTime heureSortie) {
        this.heureSortie = heureSortie;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }
}
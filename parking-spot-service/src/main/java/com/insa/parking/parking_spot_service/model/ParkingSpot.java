package com.insa.parking.parking_spot_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EMPLACEMENT") // Le nom exact de ta table SQL
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_spot") // Le nom exact de la colonne ID
    private Integer id;

    @Column(name = "nom", nullable = false, unique = true)
    private String nom; // A1, A2...

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SpotType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SpotStatus status;

    // --- ENUMS (Définis ici pour correspondre à ton SQL) ---
    public enum SpotType {
        Standard, PMR, Electrique
    }

    public enum SpotStatus {
        Libre, Réservé, Occupé
    }

    // --- GETTERS & SETTERS OBLIGATOIRES ---
    // Astuce Eclipse : Clic-droit dans le code > Source > Generate Getters and Setters > Select All > Generate
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public SpotType getType() { return type; }
    public void setType(SpotType type) { this.type = type; }
    public SpotStatus getStatus() { return status; }
    public void setStatus(SpotStatus status) { this.status = status; }
}
package com.insa.parking.payment_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "TRANSACTION")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Integer id;

    @Column(name = "id_session", nullable = false)
    private Long idSession;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "methode_paiement", nullable = false)
    private String methodePaiement;

    @Column(name = "horodatage", nullable = false)
    private LocalDateTime horodatage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    public enum PaymentStatus {
        Payé, Échec
    }

    // --- GETTERS & SETTERS OBLIGATOIRES (Je les mets pour éviter les erreurs) ---
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Long getIdSession() { return idSession; }
    public void setIdSession(Long idSession) { this.idSession = idSession; }
    
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    
    public String getMethodePaiement() { return methodePaiement; }
    public void setMethodePaiement(String methodePaiement) { this.methodePaiement = methodePaiement; }
    
    public LocalDateTime getHorodatage() { return horodatage; }
    public void setHorodatage(LocalDateTime horodatage) { this.horodatage = horodatage; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}
package com.insa.parking.payment_service.controller;

import com.insa.parking.payment_service.model.PaymentTransaction;
import com.insa.parking.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository repository;

    @GetMapping
    public List<PaymentTransaction> getAllPayments() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<PaymentTransaction> createPayment(@RequestBody PaymentTransaction transaction) {
        transaction.setHorodatage(LocalDateTime.now());
        transaction.setStatus(PaymentTransaction.PaymentStatus.Payé);
        
        PaymentTransaction saved = repository.save(transaction);
        System.out.println("💰 PAIEMENT REÇU : " + saved.getMontant() + "€ (Session " + saved.getIdSession() + ")");
        
        return ResponseEntity.ok(saved);
    }
}
package com.insa.parking.payment_service.repository;

import com.insa.parking.payment_service.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentTransaction, Integer> {
}
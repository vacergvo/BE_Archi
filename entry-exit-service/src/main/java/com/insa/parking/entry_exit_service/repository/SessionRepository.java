package com.insa.parking.entry_exit_service.repository;

import com.insa.parking.entry_exit_service.model.SessionParking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionParking, Long> {
}
package com.insa.parking.parking_spot_service.repository;

import com.insa.parking.parking_spot_service.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {

}
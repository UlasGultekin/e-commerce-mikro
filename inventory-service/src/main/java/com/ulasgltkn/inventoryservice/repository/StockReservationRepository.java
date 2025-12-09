package com.ulasgltkn.inventoryservice.repository;

import com.ulasgltkn.inventoryservice.entity.StockReservation;
import com.ulasgltkn.inventoryservice.entity.StockReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReservationRepository extends JpaRepository<StockReservation, String> {

    List<StockReservation> findByOrderId(String orderId);

    List<StockReservation> findByOrderIdAndStatus(String orderId, StockReservationStatus status);
}

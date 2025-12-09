package com.ulasgltkn.inventoryservice.service.impl;

import com.ulasgltkn.inventoryservice.dto.AdjustInventoryRequest;
import com.ulasgltkn.inventoryservice.dto.InventoryDto;
import com.ulasgltkn.inventoryservice.dto.event.InventoryEvent;
import com.ulasgltkn.inventoryservice.dto.event.OrderCreatedEvent;
import com.ulasgltkn.inventoryservice.entity.Inventory;
import com.ulasgltkn.inventoryservice.entity.StockReservation;
import com.ulasgltkn.inventoryservice.entity.StockReservationStatus;
import com.ulasgltkn.inventoryservice.exception.ResourceNotFoundException;
import com.ulasgltkn.inventoryservice.repository.InventoryRepository;
import com.ulasgltkn.inventoryservice.repository.StockReservationRepository;
import com.ulasgltkn.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository stockReservationRepository;
    private final KafkaTemplate<String, InventoryEvent> inventoryKafkaTemplate;

    @Value("${app.kafka.inventory-topic:inventory-events}")
    private String inventoryTopic;

    @Override
    @Transactional(readOnly = true)
    public InventoryDto getInventory(String productId) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .build());

        return toDto(inventory);
    }

    @Override
    public InventoryDto adjustInventory(AdjustInventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(request.getProductId())
                .orElseGet(() -> Inventory.builder()
                        .productId(request.getProductId())
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .build());

        int newAvailable = inventory.getAvailableQuantity() + request.getDelta();
        if (newAvailable < 0) {
            throw new IllegalArgumentException("Available stock cannot be negative");
        }

        inventory.setAvailableQuantity(newAvailable);
        Inventory saved = inventoryRepository.save(inventory);

        return toDto(saved);
    }

    @Override
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Basit hali: direkt rezervasyon dene
        reserveStockForOrder(event);
    }

    @Override
    public void reserveStockForOrder(OrderCreatedEvent event) {
        // Tüm ürünler için yeterli stok var mı kontrol et
        for (OrderCreatedEvent.OrderItem item : event.getItems()) {
            Inventory inv = inventoryRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + item.getProductId()));

            if (inv.getAvailableQuantity() < item.getQuantity()) {
                // Yetersiz stok: FAILED rezervasyon kaydı + STOCK_NOT_AVAILABLE event
                StockReservation reservation = StockReservation.builder()
                        .orderId(event.getOrderId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .status(StockReservationStatus.FAILED)
                        .build();
                stockReservationRepository.save(reservation);

                InventoryEvent inventoryEvent = InventoryEvent.builder()
                        .eventType("STOCK_NOT_AVAILABLE")
                        .orderId(event.getOrderId())
                        .reason("Insufficient stock for " + item.getProductId())
                        .build();
                publishInventoryEvent(inventoryEvent);
                return; // tüm siparişi reddediyoruz (basit senaryo)
            }
        }

        // Yeterli stok varsa: hepsini rezerv et
        for (OrderCreatedEvent.OrderItem item : event.getItems()) {
            Inventory inv = inventoryRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + item.getProductId()));

            inv.setAvailableQuantity(inv.getAvailableQuantity() - item.getQuantity());
            inv.setReservedQuantity(inv.getReservedQuantity() + item.getQuantity());
            inventoryRepository.save(inv);

            StockReservation reservation = StockReservation.builder()
                    .orderId(event.getOrderId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .status(StockReservationStatus.RESERVED)
                    .build();
            stockReservationRepository.save(reservation);
        }

        InventoryEvent inventoryEvent = InventoryEvent.builder()
                .eventType("STOCK_RESERVED")
                .orderId(event.getOrderId())
                .build();
        publishInventoryEvent(inventoryEvent);
    }

    @Override
    public void releaseStockForOrder(String orderId) {
        var reservations = stockReservationRepository.findByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED);

        for (StockReservation reservation : reservations) {
            Inventory inv = inventoryRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + reservation.getProductId()));

            inv.setAvailableQuantity(inv.getAvailableQuantity() + reservation.getQuantity());
            inv.setReservedQuantity(inv.getReservedQuantity() - reservation.getQuantity());
            inventoryRepository.save(inv);

            reservation.setStatus(StockReservationStatus.RELEASED);
            stockReservationRepository.save(reservation);
        }

        if (!reservations.isEmpty()) {
            InventoryEvent event = InventoryEvent.builder()
                    .eventType("STOCK_RELEASED")
                    .orderId(orderId)
                    .build();
            publishInventoryEvent(event);
        }
    }

    @Override
    public void publishInventoryEvent(InventoryEvent event) {
        inventoryKafkaTemplate.send(inventoryTopic, event.getOrderId(), event);
    }

    private InventoryDto toDto(Inventory inventory) {
        return InventoryDto.builder()
                .productId(inventory.getProductId())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .build();
    }
}

package com.ulasgltkn.inventoryservice.config;


import com.ulasgltkn.inventoryservice.dto.event.OrderCreatedEvent;
import com.ulasgltkn.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventsListener {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "${app.kafka.order-topic:order-events}",
            groupId = "inventory-service",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void onOrderCreated(OrderCreatedEvent event) {
        // ORDER_CREATED eventini al ve stok rezervasyon akışını başlat
        inventoryService.handleOrderCreated(event);
    }
}

package com.ulasgltkn.inventoryservice.service;


import com.ulasgltkn.inventoryservice.dto.AdjustInventoryRequest;
import com.ulasgltkn.inventoryservice.dto.InventoryDto;
import com.ulasgltkn.inventoryservice.dto.event.InventoryEvent;
import com.ulasgltkn.inventoryservice.dto.event.OrderCreatedEvent;

public interface InventoryService {

    InventoryDto getInventory(String productId);

    InventoryDto adjustInventory(AdjustInventoryRequest request);

    void handleOrderCreated(OrderCreatedEvent event); // Kafka consumer entry point

    void reserveStockForOrder(OrderCreatedEvent event);

    void releaseStockForOrder(String orderId);

    void publishInventoryEvent(InventoryEvent event);
}


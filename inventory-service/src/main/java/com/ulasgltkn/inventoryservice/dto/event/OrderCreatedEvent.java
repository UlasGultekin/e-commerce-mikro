package com.ulasgltkn.inventoryservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderCreatedEvent {

    private String eventType; // "ORDER_CREATED"
    private String orderId;
    private List<OrderItem> items;

    @Data
    @Builder
    public static class OrderItem {
        private String productId;
        private int quantity;
    }
}


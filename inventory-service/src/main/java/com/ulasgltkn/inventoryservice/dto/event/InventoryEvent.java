package com.ulasgltkn.inventoryservice.dto.event;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryEvent {

    private String eventType; // STOCK_RESERVED, STOCK_NOT_AVAILABLE, STOCK_RELEASED
    private String orderId;
    private String reason;    // STOCK_NOT_AVAILABLE için
}

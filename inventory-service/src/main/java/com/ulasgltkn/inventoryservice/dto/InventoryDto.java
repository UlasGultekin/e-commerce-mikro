package com.ulasgltkn.inventoryservice.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDto {

    private String productId;
    private int availableQuantity;
    private int reservedQuantity;
}

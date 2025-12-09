package com.ulasgltkn.inventoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdjustInventoryRequest {

    @NotBlank
    private String productId;

    @NotNull
    private Integer delta;

    private String reason;
}

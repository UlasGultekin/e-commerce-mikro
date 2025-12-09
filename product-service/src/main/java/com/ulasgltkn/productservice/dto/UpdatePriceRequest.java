package com.ulasgltkn.productservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePriceRequest {

    @NotNull
    private BigDecimal price;

    @NotNull
    private String currency;
}


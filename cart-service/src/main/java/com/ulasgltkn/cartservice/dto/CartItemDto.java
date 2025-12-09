package com.ulasgltkn.cartservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class CartItemDto implements Serializable {

    private String productId;
    private String name;
    private int quantity;
    private BigDecimal unitPrice;
    private String currency;
    private BigDecimal lineTotal;
}

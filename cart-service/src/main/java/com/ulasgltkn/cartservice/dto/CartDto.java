package com.ulasgltkn.cartservice.dto;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartDto implements Serializable {

    private String userId;
    private List<CartItemDto> items;
    private BigDecimal totalAmount;
    private String currency; // basit yaklaşım: ilk item’in para birimi
}

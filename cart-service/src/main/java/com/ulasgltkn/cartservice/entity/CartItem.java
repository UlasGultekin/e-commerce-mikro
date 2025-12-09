package com.ulasgltkn.cartservice.entity;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private String productId;
    private String productName;   // product-service'den doldurulabilir (opsiyonel)
    private int quantity;
    private BigDecimal unitPrice;
    private String currency;
}

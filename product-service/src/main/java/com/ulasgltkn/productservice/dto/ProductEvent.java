package com.ulasgltkn.productservice.dto;

import com.ulasgltkn.productservice.entity.ProductStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProductEvent {

    private String eventType;   // PRODUCT_CREATED, PRODUCT_UPDATED, PRICE_UPDATED ...
    private String productId;

    private String name;
    private BigDecimal price;
    private String currency;
    private ProductStatus status;
    private List<String> categories;
    private Map<String, Object> attributes;

    private Instant updatedAt;
}

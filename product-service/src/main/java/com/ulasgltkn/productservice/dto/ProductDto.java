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
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private List<String> categories;
    private Map<String, Object> attributes;
    private ProductStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}

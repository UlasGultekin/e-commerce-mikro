package com.ulasgltkn.productservice.dto;

import com.ulasgltkn.productservice.entity.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class UpdateProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private List<String> categories;
    private Map<String, Object> attributes;
    private ProductStatus status;
}


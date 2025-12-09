package com.ulasgltkn.productservice.dto;

import com.ulasgltkn.productservice.entity.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreateProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String currency;

    private List<String> categories;

    private Map<String, Object> attributes;

    @NotNull
    private ProductStatus status;
}

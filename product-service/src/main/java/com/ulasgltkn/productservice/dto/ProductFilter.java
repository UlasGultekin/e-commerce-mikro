package com.ulasgltkn.productservice.dto;

import com.ulasgltkn.productservice.entity.ProductStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductFilter {

    private String category;
    private String q; // name/description search
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private ProductStatus status;
}

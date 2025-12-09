package com.ulasgltkn.productservice.controller;

import com.ulasgltkn.productservice.dto.*;
import com.ulasgltkn.productservice.entity.ProductStatus;
import com.ulasgltkn.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // 1. GET /products?page=0&size=20&category=spor&q=ayakkabı&minPrice=100&maxPrice=1500&status=ACTIVE
    @GetMapping
    public ProductPageResponse listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ProductFilter filter = ProductFilter.builder()
                .category(category)
                .q(q)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .status(status)
                .build();

        Page<ProductDto> pageResult = productService.listProducts(filter, PageRequest.of(page, size));

        return ProductPageResponse.builder()
                .content(pageResult.getContent())
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    // 2. GET /products/{id}
    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    // 3. POST /products
    @PostMapping
    public ProductDto createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    // 4. PUT /products/{id}
    @PutMapping("/{id}")
    public ProductDto updateProduct(@PathVariable String id,
                                    @Valid @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(id, request);
    }

    // 5. PATCH /products/{id}/price
    @PatchMapping("/{id}/price")
    public ProductDto updatePrice(@PathVariable String id,
                                  @Valid @RequestBody UpdatePriceRequest request) {
        return productService.updatePrice(id, request);
    }
}


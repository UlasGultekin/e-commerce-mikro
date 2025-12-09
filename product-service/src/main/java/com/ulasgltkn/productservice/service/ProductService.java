package com.ulasgltkn.productservice.service;

import com.ulasgltkn.productservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductDto> listProducts(ProductFilter filter, Pageable pageable);

    ProductDto getProductById(String id);

    ProductDto createProduct(CreateProductRequest request);

    ProductDto updateProduct(String id, UpdateProductRequest request);

    ProductDto updatePrice(String id, UpdatePriceRequest request);

    void publishProductEvent(ProductEvent event);
}


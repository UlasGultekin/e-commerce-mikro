package com.ulasgltkn.productservice.repository;

import com.ulasgltkn.productservice.dto.ProductFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ulasgltkn.productservice.entity.Product;
public interface ProductRepositoryCustom {

    Page<Product> searchProducts(ProductFilter filter, Pageable pageable);
}


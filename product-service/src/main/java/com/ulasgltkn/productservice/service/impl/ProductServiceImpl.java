package com.ulasgltkn.productservice.service.impl;

import com.ulasgltkn.productservice.dto.*;
import com.ulasgltkn.productservice.entity.Product;
import com.ulasgltkn.productservice.entity.ProductStatus;
import com.ulasgltkn.productservice.repository.ProductRepository;
import com.ulasgltkn.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @Value("${app.kafka.product-topic:product-events}")
    private String productTopic;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> listProducts(ProductFilter filter, Pageable pageable) {
        return productRepository.searchProducts(filter, pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(String id) {
        Product product = findProductOrThrow(id);
        return toDto(product);
    }

    @Override
    public ProductDto createProduct(CreateProductRequest request) {
        Product product = new Product();
        // id yoksa Mongo kendi ObjectId üretir; sen istersen custom id de verebilirsin.
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCurrency(request.getCurrency());
        product.setCategories(request.getCategories());
        product.setAttributes(request.getAttributes());
        product.setStatus(request.getStatus() != null ? request.getStatus() : ProductStatus.ACTIVE);

        Product saved = productRepository.save(product);

        // Event publish
        ProductEvent event = toEvent(saved, "PRODUCT_CREATED");
        publishProductEvent(event);

        return toDto(saved);
    }

    @Override
    public ProductDto updateProduct(String id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCurrency() != null) product.setCurrency(request.getCurrency());
        if (request.getCategories() != null) product.setCategories(request.getCategories());
        if (request.getAttributes() != null) product.setAttributes(request.getAttributes());
        if (request.getStatus() != null) product.setStatus(request.getStatus());

        Product saved = productRepository.save(product);

        ProductEvent event = toEvent(saved, "PRODUCT_UPDATED");
        publishProductEvent(event);

        return toDto(saved);
    }

    @Override
    public ProductDto updatePrice(String id, UpdatePriceRequest request) {
        Product product = findProductOrThrow(id);

        product.setPrice(request.getPrice());
        product.setCurrency(request.getCurrency());

        Product saved = productRepository.save(product);

        ProductEvent event = toEvent(saved, "PRICE_UPDATED");
        publishProductEvent(event);

        return toDto(saved);
    }

    @Override
    public void publishProductEvent(ProductEvent event) {
        kafkaTemplate.send(productTopic, event.getProductId(), event);
    }

    // ================= PRIVATE =================

    private Product findProductOrThrow(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .categories(product.getCategories())
                .attributes(product.getAttributes())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductEvent toEvent(Product product, String eventType) {
        return ProductEvent.builder()
                .eventType(eventType)
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .status(product.getStatus())
                .categories(product.getCategories())
                .attributes(product.getAttributes())
                .updatedAt(Instant.now())
                .build();
    }
}

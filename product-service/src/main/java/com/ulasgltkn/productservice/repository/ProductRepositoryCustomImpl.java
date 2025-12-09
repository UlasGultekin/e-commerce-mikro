package com.ulasgltkn.productservice.repository;

import com.ulasgltkn.productservice.dto.ProductFilter;
import com.ulasgltkn.productservice.entity.Product;
import com.ulasgltkn.productservice.entity.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> searchProducts(ProductFilter filter, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (filter.getCategory() != null) {
            criteriaList.add(Criteria.where("categories").in(filter.getCategory()));
        }

        if (filter.getQ() != null) {
            criteriaList.add(
                    new Criteria().orOperator(
                            Criteria.where("name").regex(filter.getQ(), "i"),
                            Criteria.where("description").regex(filter.getQ(), "i")
                    )
            );
        }

        if (filter.getMinPrice() != null) {
            criteriaList.add(Criteria.where("price").gte(filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            criteriaList.add(Criteria.where("price").lte(filter.getMaxPrice()));
        }

        ProductStatus status = filter.getStatus();
        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        Criteria criteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria).with(pageable);
        List<Product> products = mongoTemplate.find(query, Product.class);

        long total = mongoTemplate.count(new Query(criteria), Product.class);

        return new org.springframework.data.domain.PageImpl<>(products, pageable, total);
    }
}


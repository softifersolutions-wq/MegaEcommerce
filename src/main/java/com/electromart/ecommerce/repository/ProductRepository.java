package com.electromart.ecommerce.repository;

import com.electromart.ecommerce.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    List<ProductEntity> findByCategoryIgnoreCase(String category);
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
}

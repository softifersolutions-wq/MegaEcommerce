package com.electromart.ecommerce.repository;

import com.electromart.ecommerce.entity.WishlistEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WishlistRepository extends MongoRepository<WishlistEntity, String> {
    Optional<WishlistEntity> findByUserId(String userId);
}

package com.electromart.ecommerce.services;

import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.entity.WishlistEntity;
import com.electromart.ecommerce.repository.ProductRepository;
import com.electromart.ecommerce.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    private WishlistEntity getOrCreate(String userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> wishlistRepository.save(new WishlistEntity(null, userId, new ArrayList<>())));
    }

    public List<ProductEntity> getWishlist(String userId) {
        WishlistEntity wishlist = getOrCreate(userId);
        return productRepository.findAllById(wishlist.getProductIds());
    }

    public List<ProductEntity> toggle(String userId, String productId) {
        WishlistEntity wishlist = getOrCreate(userId);

        if (wishlist.getProductIds().contains(productId)) {
            wishlist.getProductIds().remove(productId);
        } else {
            wishlist.getProductIds().add(productId);
        }

        wishlistRepository.save(wishlist);
        return getWishlist(userId);
    }
}

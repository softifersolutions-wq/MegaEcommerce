package com.electromart.ecommerce.controller;

import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.services.WishlistService;
import com.electromart.ecommerce.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reg/wishlist")
@RequiredArgsConstructor
    @CrossOrigin("*")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<ProductEntity>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlist(AuthUtils.currentUserId()));
    }

    @PostMapping("/toggle/{productId}")
    public ResponseEntity<List<ProductEntity>> toggle(@PathVariable String productId) {
        return ResponseEntity.ok(wishlistService.toggle(AuthUtils.currentUserId(), productId));
    }
}

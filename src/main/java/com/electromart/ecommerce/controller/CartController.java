package com.electromart.ecommerce.controller;

import com.electromart.ecommerce.dto.ApiResponse;
import com.electromart.ecommerce.dto.CartItemRequest;
import com.electromart.ecommerce.dto.CartResponse;
import com.electromart.ecommerce.services.CartService;
import com.electromart.ecommerce.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reg/cart")
@RequiredArgsConstructor
    @CrossOrigin("*")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(AuthUtils.currentUserId()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(AuthUtils.currentUserId(), request));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable String productId,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(cartService.updateItem(AuthUtils.currentUserId(), productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable String productId) {
        return ResponseEntity.ok(cartService.removeItem(AuthUtils.currentUserId(), productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearCart() {
        cartService.clearCart(AuthUtils.currentUserId());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared"));
    }
}

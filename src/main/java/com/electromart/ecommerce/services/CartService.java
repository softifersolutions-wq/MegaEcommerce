package com.electromart.ecommerce.services;

import com.electromart.ecommerce.dto.CartItemRequest;
import com.electromart.ecommerce.dto.CartResponse;
import com.electromart.ecommerce.entity.CartEntity;
import com.electromart.ecommerce.entity.CartItemEntity;
import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.exception.ResourceNotFoundException;
import com.electromart.ecommerce.repository.CartRepository;
import com.electromart.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    private CartEntity getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new CartEntity(null, userId, new java.util.ArrayList<>())));
    }

    public CartResponse getCart(String userId) {
        CartEntity cart = getOrCreateCart(userId);
        return toResponse(cart);
    }

    public CartResponse addItem(String userId, CartItemRequest request) {
        CartEntity cart = getOrCreateCart(userId);

        // Validate product exists
        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        Optional<CartItemEntity> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.getQuantity());
        } else {
            cart.getItems().add(new CartItemEntity(request.getProductId(), request.getQuantity()));
        }

        return toResponse(cartRepository.save(cart));
    }

    public CartResponse updateItem(String userId, String productId, int quantity) {
        CartEntity cart = getOrCreateCart(userId);

        if (quantity <= 0) {
            cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        } else {
            cart.getItems().stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .findFirst()
                    .ifPresent(i -> i.setQuantity(quantity));
        }

        return toResponse(cartRepository.save(cart));
    }

    public CartResponse removeItem(String userId, String productId) {
        CartEntity cart = getOrCreateCart(userId);
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        return toResponse(cartRepository.save(cart));
    }

    public void clearCart(String userId) {
        CartEntity cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartResponse toResponse(CartEntity cart) {
        List<CartResponse.Item> items = cart.getItems().stream()
                .map(i -> {
                    ProductEntity product = productRepository.findById(i.getProductId()).orElse(null);
                    return new CartResponse.Item(product, i.getQuantity());
                })
                .filter(item -> item.getProduct() != null)
                .collect(Collectors.toList());

        double subtotal = items.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        return new CartResponse(items, subtotal);
    }
}

package com.electromart.ecommerce.services;

import com.electromart.ecommerce.dto.OrderRequest;
import com.electromart.ecommerce.entity.*;
import com.electromart.ecommerce.exception.BadRequestException;
import com.electromart.ecommerce.exception.ResourceNotFoundException;
import com.electromart.ecommerce.repository.CartRepository;
import com.electromart.ecommerce.repository.OrderRepository;
import com.electromart.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderEntity placeOrder(String userId, OrderRequest request) {
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Your cart is empty."));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty.");
        }

        List<OrderItemEntity> orderItems = cart.getItems().stream()
                .map(item -> {
                    ProductEntity product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
                    return new OrderItemEntity(
                            product.getId(), product.getName(), product.getImage(),
                            product.getPrice(), item.getQuantity()
                    );
                })
                .collect(Collectors.toList());

        double subtotal = orderItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        double shippingCost = subtotal >= 50 ? 0 : 9.99;
        double total = subtotal + shippingCost;

        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setShippingAddress(request.getShippingAddress());
        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTotal(total);
        order.setStatus("Processing");

        OrderEntity saved = orderRepository.save(order);

        // Clear the cart after a successful order
        cart.getItems().clear();
        cartRepository.save(cart);

        return saved;
    }

    public List<OrderEntity> getOrdersForUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public OrderEntity updateStatus(String orderId, String status) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}

package com.electromart.ecommerce.controller;

import com.electromart.ecommerce.dto.OrderRequest;
import com.electromart.ecommerce.entity.OrderEntity;
import com.electromart.ecommerce.services.OrderService;
import com.electromart.ecommerce.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reg/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderEntity> placeOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(AuthUtils.currentUserId(), request));
    }

    @GetMapping
    public ResponseEntity<List<OrderEntity>> getMyOrders() {
        return ResponseEntity.ok(orderService.getOrdersForUser(AuthUtils.currentUserId()));
    }
}

package com.electromart.ecommerce.controller;

import com.electromart.ecommerce.dto.AdminStatsResponse;
import com.electromart.ecommerce.dto.ApiResponse;
import com.electromart.ecommerce.dto.OrderStatusUpdateRequest;
import com.electromart.ecommerce.dto.ProductRequest;
import com.electromart.ecommerce.entity.OrderEntity;
import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.repository.ProductRepository;
import com.electromart.ecommerce.repository.UserRepository;
import com.electromart.ecommerce.services.OrderService;
import com.electromart.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/** All endpoints here require ROLE_ADMIN (enforced in SpringSecurity via /admin/**). */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
    @CrossOrigin("*")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @PostMapping(value = "/products", consumes = "multipart/form-data")
    public ResponseEntity<ProductEntity> createProduct(
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(productService.create(request, image));
    }

    @PutMapping(value = "/products/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductEntity> updateProduct(
            @PathVariable String id,
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(productService.update(id, request, image));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully"));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderEntity> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }

    /** Backs the admin dashboard's summary cards. */
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        List<OrderEntity> orders = orderService.getAllOrders();
        long totalProducts = productRepository.count();
        long totalOrders = orders.size();
        long totalUsers = userRepository.count();
        double totalRevenue = orders.stream().mapToDouble(OrderEntity::getTotal).sum();

        return ResponseEntity.ok(new AdminStatsResponse(totalProducts, totalOrders, totalUsers, totalRevenue));
    }
}

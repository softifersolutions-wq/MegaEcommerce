package com.electromart.ecommerce.controller;

import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Public, read-only product endpoints. Product management lives in AdminController. */
@RestController
@RequestMapping("/public/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductEntity>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(productService.getAll(category, brand, search, maxPrice, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(productService.getCategories());
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getBrands() {
        return ResponseEntity.ok(productService.getBrands());
    }
}

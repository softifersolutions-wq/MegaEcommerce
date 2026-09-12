package com.electromart.ecommerce.controller;

import com.electromart.ecommerce.entity.BlogEntity;
import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.services.BlogService;
import com.electromart.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Public, read-only product endpoints. Product management lives in AdminController. */
@RestController
@RequestMapping("/public/products")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;
@Autowired
private BlogService blogService;
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
     @GetMapping("/all")
    public ResponseEntity<List<BlogEntity>> fetchPublicFeed() {
        try {
            List<BlogEntity> blogs = blogService.getAllBlogs();
            return new ResponseEntity<>(blogs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Is code function block ko Controller array mappings me shamil karein
    @GetMapping("/byslug/{slug}")
    public ResponseEntity<?> getBlogBySlug(@PathVariable String slug) {
        try {
            // Id se hatakar service layer ko custom slug string matrix forward karna
            BlogEntity blog = blogService.getBlogBySlug(slug);
            return new ResponseEntity<>(blog, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server parsing error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

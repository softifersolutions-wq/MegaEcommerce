package com.electromart.ecommerce.services;

import com.electromart.ecommerce.dto.ProductRequest;
import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.exception.ResourceNotFoundException;
import com.electromart.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    public List<ProductEntity> getAll(String category, String brand, String search, Double maxPrice, String sort) {
        List<ProductEntity> products = productRepository.findAll();

        if (category != null && !category.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }
        if (brand != null && !brand.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getBrand().equalsIgnoreCase(brand))
                    .collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String term = search.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(term)
                            || p.getCategory().toLowerCase().contains(term)
                            || p.getBrand().toLowerCase().contains(term))
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case "price-asc" -> products.sort(Comparator.comparingDouble(ProductEntity::getPrice));
                case "price-desc" -> products.sort(Comparator.comparingDouble(ProductEntity::getPrice).reversed());
                case "rating" -> products.sort(Comparator.comparingDouble(ProductEntity::getRating).reversed());
                default -> { /* featured: leave insertion order */ }
            }
        }

        return products;
    }

    public ProductEntity getById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public List<String> getCategories() {
        return productRepository.findAll().stream()
                .map(ProductEntity::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getBrands() {
        return productRepository.findAll().stream()
                .map(ProductEntity::getBrand)
                .distinct()
                .collect(Collectors.toList());
    }

    public ProductEntity create(ProductRequest request, MultipartFile image) throws IOException {
        ProductEntity product = new ProductEntity();
        applyRequest(product, request);

        if (image != null && !image.isEmpty()) {
            product.setImage(cloudinaryService.uploadImage(image));
        }

        return productRepository.save(product);
    }

    public ProductEntity update(String id, ProductRequest request, MultipartFile image) throws IOException {
        ProductEntity product = getById(id);
        applyRequest(product, request);

        if (image != null && !image.isEmpty()) {
            product.setImage(cloudinaryService.uploadImage(image));
        }

        return productRepository.save(product);
    }

    public void delete(String id) {
        ProductEntity product = getById(id);
        productRepository.delete(product);
    }

    private void applyRequest(ProductEntity product, ProductRequest request) {
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setOldPrice(request.getOldPrice());
        product.setDescription(request.getDescription());
        product.setSpecs(request.getSpecs());
        product.setStock(request.getStock());
        product.setTag(request.getTag());
    }
}

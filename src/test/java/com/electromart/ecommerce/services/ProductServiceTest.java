package com.electromart.ecommerce.services;

import com.electromart.ecommerce.dto.ProductRequest;
import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.exception.ResourceNotFoundException;
import com.electromart.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private ProductService productService;

    private ProductEntity phone;
    private ProductEntity laptop;

    @BeforeEach
    void setUp() {
        phone = new ProductEntity();
        phone.setId("p1");
        phone.setName("iPhone 15");
        phone.setCategory("Mobiles");
        phone.setBrand("Apple");
        phone.setPrice(999.0);
        phone.setRating(4.5);

        laptop = new ProductEntity();
        laptop.setId("p2");
        laptop.setName("MacBook Air");
        laptop.setCategory("Laptops");
        laptop.setBrand("Apple");
        laptop.setPrice(1299.0);
        laptop.setRating(4.8);
    }

    // ---------- getAll ----------

    @Test
    void getAll_noFilters_returnsEverything() {
        when(productRepository.findAll()).thenReturn(List.of(phone, laptop));

        List<ProductEntity> result = productService.getAll(null, null, null, null, null);

        assertThat(result).containsExactlyInAnyOrder(phone, laptop);
    }

    @Test
    void getAll_filtersByCategory_caseInsensitive() {
        when(productRepository.findAll()).thenReturn(List.of(phone, laptop));

        List<ProductEntity> result = productService.getAll("mobiles", null, null, null, null);

        assertThat(result).containsExactly(phone);
    }

    @Test
    void getAll_filtersByBrand() {
        when(productRepository.findAll()).thenReturn(List.of(phone, laptop));

        List<ProductEntity> result = productService.getAll(null, "Apple", null, null, null);

        assertThat(result).containsExactlyInAnyOrder(phone, laptop);
    }

    @Test
    void getAll_filtersBySearchTerm_matchesNameCategoryOrBrand() {
        when(productRepository.findAll()).thenReturn(List.of(phone, laptop));

        List<ProductEntity> result = productService.getAll(null, null, "macbook", null, null);

        assertThat(result).containsExactly(laptop);
    }

    @Test
    void getAll_filtersByMaxPrice() {
        when(productRepository.findAll()).thenReturn(List.of(phone, laptop));

        List<ProductEntity> result = productService.getAll(null, null, null, 1000.0, null);

        assertThat(result).containsExactly(phone);
    }

    @Test
    void getAll_sortsByPriceAscending() {
        // Mutable list: when no filters run, getAll() sorts findAll()'s result in place.
        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(laptop, phone)));

        List<ProductEntity> result = productService.getAll(null, null, null, null, "price-asc");

        assertThat(result).containsExactly(phone, laptop);
    }

    @Test
    void getAll_sortsByPriceDescending() {
        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(phone, laptop)));

        List<ProductEntity> result = productService.getAll(null, null, null, null, "price-desc");

        assertThat(result).containsExactly(laptop, phone);
    }

    @Test
    void getAll_sortsByRatingDescending() {
        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(phone, laptop)));

        List<ProductEntity> result = productService.getAll(null, null, null, null, "rating");

        assertThat(result).containsExactly(laptop, phone);
    }

    // ---------- getById ----------

    @Test
    void getById_returnsProduct_whenFound() {
        when(productRepository.findById("p1")).thenReturn(Optional.of(phone));

        ProductEntity result = productService.getById("p1");

        assertThat(result).isEqualTo(phone);
    }

    @Test
    void getById_throwsResourceNotFound_whenMissing() {
        when(productRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById("missing"));
    }

    // ---------- getCategories / getBrands ----------

    @Test
    void getCategories_returnsDistinctValues() {
        when(productRepository.findAll()).thenReturn(List.of(phone, laptop));

        List<String> result = productService.getCategories();

        assertThat(result).containsExactlyInAnyOrder("Mobiles", "Laptops");
    }

    @Test
    void getBrands_returnsDistinctValues() {
        when(productRepository.findAll()).thenReturn(List.of(phone, laptop));

        List<String> result = productService.getBrands();

        assertThat(result).containsExactly("Apple");
    }

    // ---------- create ----------

    @Test
    void create_savesProduct_withoutImage() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Pixel 9");
        request.setCategory("Mobiles");
        request.setBrand("Google");
        request.setPrice(799.0);
        request.setStock(10);

        when(productRepository.save(any(ProductEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductEntity result = productService.create(request, null);

        assertThat(result.getName()).isEqualTo("Pixel 9");
        assertThat(result.getImage()).isNull();
        verify(cloudinaryService, never()).uploadImage(any());
    }

    @Test
    void create_uploadsImage_whenProvided() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Pixel 9");
        request.setCategory("Mobiles");
        request.setBrand("Google");
        request.setPrice(799.0);

        MultipartFile image = new MockMultipartFile("image", "pixel.jpg", "image/jpeg", new byte[]{1, 2, 3});

        when(cloudinaryService.uploadImage(image)).thenReturn("https://cloudinary.test/pixel.jpg");
        when(productRepository.save(any(ProductEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductEntity result = productService.create(request, image);

        assertThat(result.getImage()).isEqualTo("https://cloudinary.test/pixel.jpg");
        verify(cloudinaryService).uploadImage(image);
    }

    // ---------- update ----------

    @Test
    void update_appliesChanges_toExistingProduct() throws Exception {
        when(productRepository.findById("p1")).thenReturn(Optional.of(phone));
        when(productRepository.save(any(ProductEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductRequest request = new ProductRequest();
        request.setName("iPhone 15 Pro");
        request.setCategory("Mobiles");
        request.setBrand("Apple");
        request.setPrice(1099.0);

        ProductEntity result = productService.update("p1", request, null);

        assertThat(result.getName()).isEqualTo("iPhone 15 Pro");
        assertThat(result.getPrice()).isEqualTo(1099.0);
    }

    @Test
    void update_throwsResourceNotFound_whenProductMissing() {
        when(productRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.update("missing", new ProductRequest(), null));
    }

    // ---------- delete ----------

    @Test
    void delete_removesProduct_whenFound() {
        when(productRepository.findById("p1")).thenReturn(Optional.of(phone));

        productService.delete("p1");

        verify(productRepository).delete(phone);
    }

    @Test
    void delete_throwsResourceNotFound_whenMissing() {
        when(productRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.delete("missing"));
    }
}
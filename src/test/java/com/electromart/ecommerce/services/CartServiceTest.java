package com.electromart.ecommerce.services;

import com.electromart.ecommerce.dto.CartItemRequest;
import com.electromart.ecommerce.dto.CartResponse;
import com.electromart.ecommerce.entity.CartEntity;
import com.electromart.ecommerce.entity.CartItemEntity;
import com.electromart.ecommerce.entity.ProductEntity;
import com.electromart.ecommerce.exception.ResourceNotFoundException;
import com.electromart.ecommerce.repository.CartRepository;
import com.electromart.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private ProductEntity phone;

    @BeforeEach
    void setUp() {
        phone = new ProductEntity();
        phone.setId("p1");
        phone.setName("iPhone 15");
        phone.setPrice(1000.0);
    }

    // ---------- getCart ----------

    @Test
    void getCart_createsEmptyCart_whenNoneExists() {
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.getCart("u1");

        assertThat(response.getItems()).isEmpty();
        assertThat(response.getSubtotal()).isZero();
        verify(cartRepository).save(argThat(c -> c.getUserId().equals("u1") && c.getItems().isEmpty()));
    }

    @Test
    void getCart_returnsExistingItems_withComputedSubtotal() {
        CartEntity cart = new CartEntity("c1", "u1", new ArrayList<>(List.of(new CartItemEntity("p1", 2))));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(productRepository.findById("p1")).thenReturn(Optional.of(phone));

        CartResponse response = cartService.getCart("u1");

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(response.getSubtotal()).isEqualTo(2000.0);
    }

    @Test
    void getCart_skipsItems_forDeletedProducts() {
        CartEntity cart = new CartEntity("c1", "u1", new ArrayList<>(List.of(new CartItemEntity("gone", 1))));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(productRepository.findById("gone")).thenReturn(Optional.empty());

        CartResponse response = cartService.getCart("u1");

        assertThat(response.getItems()).isEmpty();
        assertThat(response.getSubtotal()).isZero();
    }

    // ---------- addItem ----------

    @Test
    void addItem_addsNewLine_whenProductNotAlreadyInCart() {
        CartEntity cart = new CartEntity("c1", "u1", new ArrayList<>());
        CartItemRequest request = new CartItemRequest();
        request.setProductId("p1");
        request.setQuantity(3);

        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(productRepository.findById("p1")).thenReturn(Optional.of(phone));
        when(cartRepository.save(any(CartEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addItem("u1", request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void addItem_mergesQuantity_whenProductAlreadyInCart() {
        CartEntity cart = new CartEntity("c1", "u1", new ArrayList<>(List.of(new CartItemEntity("p1", 2))));
        CartItemRequest request = new CartItemRequest();
        request.setProductId("p1");
        request.setQuantity(3);

        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(productRepository.findById("p1")).thenReturn(Optional.of(phone));
        when(cartRepository.save(any(CartEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addItem("u1", request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void addItem_throwsResourceNotFound_whenProductDoesNotExist() {
        CartEntity cart = new CartEntity("c1", "u1", new ArrayList<>());
        CartItemRequest request = new CartItemRequest();
        request.setProductId("missing");
        request.setQuantity(1);

        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(productRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addItem("u1", request));
        verify(cartRepository, never()).save(any());
    }

    // ---------- updateItem ----------

    @Test
    void updateItem_changesQuantity_whenPositive() {
        CartEntity cart = new CartEntity("c1", "u1", new ArrayList<>(List.of(new CartItemEntity("p1", 1))));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(productRepository.findById("p1")).thenReturn(Optional.of(phone));
        when(cartRepository.save(any(CartEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.updateItem("u1", "p1", 5);

        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void updateItem_removesLine_whenQuantityIsZeroOrLess() {
        CartEntity cart = new CartEntity("c1", "u1", new ArrayList<>(List.of(new CartItemEntity("p1", 1))));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(CartEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.updateItem("u1", "p1", 0);

        assertThat(response.getItems()).isEmpty();
        verify(productRepository, never()).findById(any());
    }

    // ---------- removeItem ----------

    @Test
    void removeItem_deletesMatchingLine() {
        CartEntity cart = new CartEntity("c1", "u1",
                new ArrayList<>(List.of(new CartItemEntity("p1", 1), new CartItemEntity("p2", 1))));
        ProductEntity macbook = new ProductEntity();
        macbook.setId("p2");
        macbook.setName("MacBook");
        macbook.setPrice(1500.0);

        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(productRepository.findById("p2")).thenReturn(Optional.of(macbook));
        when(cartRepository.save(any(CartEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.removeItem("u1", "p1");

        assertThat(response.getItems()).hasSize(1);
        verify(cartRepository).save(argThat(c -> c.getItems().size() == 1
                && c.getItems().get(0).getProductId().equals("p2")));
    }

    // ---------- clearCart ----------

    @Test
    void clearCart_emptiesItems_andSaves() {
        CartEntity cart = new CartEntity("c1", "u1", new ArrayList<>(List.of(new CartItemEntity("p1", 2))));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));

        cartService.clearCart("u1");

        verify(cartRepository).save(argThat(c -> c.getItems().isEmpty()));
    }
}

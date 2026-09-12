package com.electromart.ecommerce.dto;

import com.electromart.ecommerce.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private List<Item> items;
    private double subtotal;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private ProductEntity product;
        private int quantity;
    }
}

package com.electromart.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Embedded document inside CartEntity — not a top-level collection. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemEntity {
    private String productId;
    private int quantity;
}

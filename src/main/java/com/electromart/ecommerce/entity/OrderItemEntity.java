package com.electromart.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Snapshot of a product at the time the order was placed. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {
    private String productId;
    private String name;
    private String image;
    private double price;
    private int quantity;
}

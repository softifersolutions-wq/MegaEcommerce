package com.electromart.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class OrderEntity {

    @Id
    private String id;

    @Indexed
    private String userId;

    private List<OrderItemEntity> items;

    private ShippingAddressEntity shippingAddress;

    private double subtotal;

    private double shippingCost;

    private double total;

    private String status;

    @CreatedDate
    private Instant createdAt;
}
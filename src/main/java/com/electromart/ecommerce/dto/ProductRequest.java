package com.electromart.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

/** Used by admin endpoints to create/update a product (image uploaded separately as multipart). */
@Data
public class ProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotBlank
    private String brand;

    @PositiveOrZero
    private double price;

    private Double oldPrice;

    private String description;

    private List<String> specs;

    @PositiveOrZero
    private int stock;

    private String tag;
}

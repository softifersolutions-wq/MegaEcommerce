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
@Document(collection = "products")
public class ProductEntity {

    @Id
    private String id;

    private String name;

    @Indexed
    private String category;

    @Indexed
    private String brand;

    private double price;

    private Double oldPrice;

    private double rating;

    private int reviews;

    /** Cloudinary secure URL */
    private String image;

    private String description;

    private List<String> specs;

    private int stock;

    /** New | Best Seller | Sale | Limited */
    private String tag;

    @CreatedDate
    private Instant createdAt;
}

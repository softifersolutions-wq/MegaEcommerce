package com.electromart.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlists")
public class WishlistEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private List<String> productIds = new ArrayList<>();
}

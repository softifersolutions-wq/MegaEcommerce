package com.electromart.ecommerce.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blogs")
public class BlogEntity {

    @Id
    private String id;
    private String title;
    private String slug;
    private String category;
    private String author;
    private String readTime;
    private String summary;
    private String content;

    // NEW: Variable to hold file path or URL for Multipart image payload sync
    private String image;

    private String metaKeywords;
    private String metaDescription;
    private boolean isNoIndex;
    private String date; // Client side processed date (e.g., Aug 08, 2026)
    private String status;
}


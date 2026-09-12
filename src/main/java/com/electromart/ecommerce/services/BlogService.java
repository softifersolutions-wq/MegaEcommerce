package com.electromart.ecommerce.services;


import com.electromart.ecommerce.entity.BlogEntity;
import com.electromart.ecommerce.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    // Save a new blog metadata object
    public BlogEntity saveBlogPost(BlogEntity blog) {

        System.out.println("========== SAVE BLOG ==========");
        System.out.println("TITLE BEFORE SLUG = [" + blog.getTitle() + "]");
        System.out.println("SLUG BEFORE SLUG = [" + blog.getSlug() + "]");

        if (blog.getStatus() == null) {
            blog.setStatus("PUBLISHED");
        }

        if (blog.getTitle() != null && !blog.getTitle().isBlank()) {

            String generatedSlug = blog.getTitle()
                    .toLowerCase()
                    .trim()
                    .replaceAll("[^\\w\\s]", "")
                    .replaceAll("\\s+", " ");

            blog.setSlug(generatedSlug);
        }

        System.out.println("FINAL SLUG = [" + blog.getSlug() + "]");

        return blogRepository.save(blog);
    }

    // Get all system logs mapping for public feed page component reads
    public List<BlogEntity> getAllBlogs() {
        return blogRepository.findAll();
    }
    // Is function method logic ko core service pipeline me paste karein
    public BlogEntity getBlogBySlug(String slug) {
        if (slug == null) {
            throw new RuntimeException("Slug trajectory query cannot be empty.");
        }

        // 🔥 Trick: Dashes (-) ko wapas regular spaces ( ) mein tabdeel karein
        String dbCompatibleTitle = slug.replace("-", " ");

        // Ab Repository mein findBySlug ki jagah direct findByTitle ya findBySlug par pass karein
        // (Dhyaan rakhein ke aapki check query exact match karegi)
        return blogRepository.findBySlug(dbCompatibleTitle)
                .orElseThrow(() -> new RuntimeException("Article not found with title match: " + dbCompatibleTitle));
    }


}


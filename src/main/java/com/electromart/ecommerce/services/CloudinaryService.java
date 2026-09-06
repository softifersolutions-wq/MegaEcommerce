package com.electromart.ecommerce.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Uploads an image and returns its secure URL.
     */
    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", "electromart/products",
                "public_id", UUID.randomUUID().toString(),
                "resource_type", "image"
        );
        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) result.get("secure_url");
    }
}

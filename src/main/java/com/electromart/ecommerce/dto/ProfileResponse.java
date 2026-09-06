package com.electromart.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String id;
    private String name;
    private String email;
    private String role;
    private Instant createdAt;
}

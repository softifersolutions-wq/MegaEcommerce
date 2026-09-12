package com.electromart.ecommerce.services;

import com.electromart.ecommerce.dto.ProfileResponse;
import com.electromart.ecommerce.dto.ProfileUpdateRequest;
import com.electromart.ecommerce.entity.UserEntity;
import com.electromart.ecommerce.exception.ResourceNotFoundException;
import com.electromart.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;

    public ProfileResponse getProfile(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return toResponse(user);
    }

    public ProfileResponse updateProfile(String userId, ProfileUpdateRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        user.setName(request.getName());
        UserEntity saved = userRepository.save(user);
        return toResponse(saved);
    }

    private ProfileResponse toResponse(UserEntity user) {
        return new ProfileResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt()
        );
    }
}

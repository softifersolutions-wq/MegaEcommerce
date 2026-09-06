package com.electromart.ecommerce.controller;

import com.electromart.ecommerce.dto.ProfileResponse;
import com.electromart.ecommerce.dto.ProfileUpdateRequest;
import com.electromart.ecommerce.services.AccountService;
import com.electromart.ecommerce.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Backs the customer dashboard's profile card. */
@RestController
@RequestMapping("/reg/account")
@RequiredArgsConstructor
    @CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(accountService.getProfile(AuthUtils.currentUserId()));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(accountService.updateProfile(AuthUtils.currentUserId(), request));
    }
}

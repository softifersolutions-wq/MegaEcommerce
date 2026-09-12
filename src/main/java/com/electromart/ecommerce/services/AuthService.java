package com.electromart.ecommerce.services;

import com.electromart.ecommerce.dto.LoginRequest;
import com.electromart.ecommerce.dto.LoginResponse;
import com.electromart.ecommerce.dto.SignupRequest;
import com.electromart.ecommerce.entity.UserEntity;
import com.electromart.ecommerce.exception.BadRequestException;
import com.electromart.ecommerce.repository.UserRepository;
import com.electromart.ecommerce.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public LoginResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists.");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        UserEntity saved = userRepository.save(user);
        String token = jwtUtils.generateToken(saved.getEmail());

        return new LoginResponse(token, saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword())
        );

        UserEntity user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        String token = jwtUtils.generateToken(user.getEmail());
        return new LoginResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}

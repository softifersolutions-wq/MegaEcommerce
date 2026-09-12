package com.electromart.ecommerce.services;

import com.electromart.ecommerce.dto.LoginRequest;
import com.electromart.ecommerce.dto.LoginResponse;
import com.electromart.ecommerce.dto.SignupRequest;
import com.electromart.ecommerce.entity.UserEntity;
import com.electromart.ecommerce.exception.BadRequestException;
import com.electromart.ecommerce.repository.UserRepository;
import com.electromart.ecommerce.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    // ---------- signup ----------

    @Test
    void signup_createsUser_andReturnsToken() {
        SignupRequest request = new SignupRequest();
        request.setName("Ali Raza");
        request.setEmail("Ali@Example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("Ali@Example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setId("u1");
            return u;
        });
        when(jwtUtils.generateToken("ali@example.com")).thenReturn("jwt-token");

        LoginResponse response = authService.signup(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getId()).isEqualTo("u1");
        assertThat(response.getEmail()).isEqualTo("ali@example.com");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");

        verify(userRepository).save(argThat(u ->
                u.getEmail().equals("ali@example.com")
                        && u.getPassword().equals("hashed-secret")
                        && u.getRole().equals("ROLE_USER")));
    }

    @Test
    void signup_throwsBadRequest_whenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest();
        request.setName("Ali Raza");
        request.setEmail("ali@example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("ali@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.signup(request));
        verify(userRepository, never()).save(any());
    }

    // ---------- login ----------

    @Test
    void login_returnsToken_onValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("Ali@Example.com");
        request.setPassword("secret123");

        UserEntity user = new UserEntity("u1", "Ali Raza", "ali@example.com", "hashed-secret", "ROLE_USER", null);

        when(userRepository.findByEmail("ali@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken("ali@example.com")).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getId()).isEqualTo("u1");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("ali@example.com", "secret123"));
    }

    @Test
    void login_throwsBadRequest_whenUserVanishesAfterAuthentication() {
        // Edge case guarded by the service: AuthenticationManager passed, but the
        // user record can't be found on the immediate re-lookup.
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.login(request));
        verify(jwtUtils, never()).generateToken(anyString());
    }
}

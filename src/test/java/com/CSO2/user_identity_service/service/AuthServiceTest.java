package com.CSO2.user_identity_service.service;

import com.CSO2.user_identity_service.config.JwtUtil;
import com.CSO2.user_identity_service.dto.request.LoginRequest;
import com.CSO2.user_identity_service.dto.request.RegisterRequest;
import com.CSO2.user_identity_service.dto.response.AuthenticationResponse;
import com.CSO2.user_identity_service.model.User;
import com.CSO2.user_identity_service.model.enums.Role;
import com.CSO2.user_identity_service.model.enums.Tier;
import com.CSO2.user_identity_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFullName("Test User");
        registerRequest.setPhone("1234567890");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        user = User.builder()
                .id("1")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .fullName("Test User")
                .role(Role.BUYER)
                .tier(Tier.BRONZE)
                .build();

        userDetails = mock(UserDetails.class);
    }

    @Test
    void register_ShouldReturnAuthenticationResponse_WhenEmailIsUnique() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class), anyString())).thenReturn("jwtToken");
        when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        AuthenticationResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("test@example.com", response.getUser().getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnAuthenticationResponse_WhenCredentialsAreValid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class), anyString())).thenReturn("jwtToken");
        when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        AuthenticationResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void refresh_ShouldReturnNewToken_WhenRefreshTokenIsValid() {
        String refreshToken = "validRefreshToken";
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtUtil.validateToken(refreshToken, userDetails)).thenReturn(true);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(userDetails, "BUYER")).thenReturn("newJwtToken");
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("newRefreshToken");

        AuthenticationResponse response = authService.refresh(refreshToken);

        assertNotNull(response);
        assertEquals("newJwtToken", response.getToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
    }

    @Test
    void refresh_ShouldThrowException_WhenRefreshTokenIsInvalid() {
        String refreshToken = "invalidRefreshToken";
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtUtil.validateToken(refreshToken, userDetails)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.refresh(refreshToken));
    }
}

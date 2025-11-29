package com.CSO2.user_identity_service.service;

import com.CSO2.user_identity_service.config.JwtUtil;
import com.CSO2.user_identity_service.dto.AuthenticationResponse;
import com.CSO2.user_identity_service.dto.LoginRequest;
import com.CSO2.user_identity_service.dto.RegisterRequest;
import com.CSO2.user_identity_service.dto.UserSummaryDTO;
import com.CSO2.user_identity_service.model.User;
import com.CSO2.user_identity_service.model.enums.Role;
import com.CSO2.user_identity_service.model.enums.Tier;
import com.CSO2.user_identity_service.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .preferredStore(request.getPreferredStore())
                .role(Role.BUYER) // Default role
                .tier(Tier.BRONZE) // Default tier
                .loyaltyPoints(0)
                .newsletterSubscribed(false)
                .build();

        userRepository.save(user);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails, user.getRole().name());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .user(mapToUserSummary(user))
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails, user.getRole().name());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .user(mapToUserSummary(user))
                .build();
    }

    public AuthenticationResponse refresh(String refreshToken) {
        String email = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            User user = userRepository.findByEmail(email).orElseThrow();
            String newJwt = jwtUtil.generateToken(userDetails, user.getRole().name());

            return AuthenticationResponse.builder()
                    .token(newJwt)
                    .refreshToken(refreshToken) // Return same refresh token or rotate? Plan said rotate, but for now
                                                // keeping simple or I can rotate.
                    // Let's rotate if needed, but usually refresh token is long lived.
                    // If I want to rotate, I generate a new one.
                    // Let's generate a new one for better security.
                    .refreshToken(jwtUtil.generateRefreshToken(userDetails))
                    .user(mapToUserSummary(user))
                    .build();
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    private UserSummaryDTO mapToUserSummary(User user) {
        return UserSummaryDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}

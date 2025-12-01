package com.CSO2.user_identity_service.service;

import com.CSO2.user_identity_service.dto.request.ChangePasswordRequest;
import com.CSO2.user_identity_service.dto.request.UpdateProfileRequest;
import com.CSO2.user_identity_service.dto.response.UserDetailDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("1")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .fullName("Test User")
                .role(Role.BUYER)
                .tier(Tier.BRONZE)
                .loyaltyPoints(0)
                .newsletterSubscribed(false)
                .build();
    }

    @Test
    void getProfile_ShouldReturnUserDetailDTO_WhenUserExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        UserDetailDTO result = userService.getProfile("1");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getProfileByEmail_ShouldReturnUserDetailDTO_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetailDTO result = userService.getProfileByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void updateProfile_ShouldUpdateUser_WhenUserExists() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated Name");
        request.setPhone("0987654321");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDetailDTO result = userService.updateProfile("1", request);

        assertNotNull(result);
        assertEquals("Updated Name", result.getFullName());
        assertEquals("0987654321", result.getPhone());
    }

    @Test
    void updateProfileByEmail_ShouldUpdateUser_WhenUserExists() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated Name");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDetailDTO result = userService.updateProfileByEmail("test@example.com", request);

        assertNotNull(result);
        assertEquals("Updated Name", result.getFullName());
    }

    @Test
    void changePassword_ShouldChangePassword_WhenOldPasswordIsCorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        userService.changePassword("1", request);

        verify(userRepository).save(user);
        assertEquals("newEncodedPassword", user.getPasswordHash());
    }

    @Test
    void changePasswordByEmail_ShouldChangePassword_WhenOldPasswordIsCorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        userService.changePasswordByEmail("test@example.com", request);

        verify(userRepository).save(user);
        assertEquals("newEncodedPassword", user.getPasswordHash());
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordIsIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongPassword");
        request.setNewPassword("newPassword");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.changePassword("1", request));
    }
}

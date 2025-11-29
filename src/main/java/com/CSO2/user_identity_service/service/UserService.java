package com.CSO2.user_identity_service.service;

import com.CSO2.user_identity_service.dto.request.ChangePasswordRequest;
import com.CSO2.user_identity_service.dto.request.UpdateProfileRequest;
import com.CSO2.user_identity_service.dto.response.UserDetailDTO;
import com.CSO2.user_identity_service.model.User;
import com.CSO2.user_identity_service.model.enums.Tier;
import com.CSO2.user_identity_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetailDTO getProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserDetailDTO(user);
    }

    public UserDetailDTO getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserDetailDTO(user);
    }

    public UserDetailDTO updateProfile(String userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return updateUser(user, req);
    }

    public UserDetailDTO updateProfileByEmail(String email, UpdateProfileRequest req) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return updateUser(user, req);
    }

    private UserDetailDTO updateUser(User user, UpdateProfileRequest req) {
        if (req.getFullName() != null) {
            user.setFullName(req.getFullName());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }
        if (req.getNewsletterSubscribed() != null) {
            user.setNewsletterSubscribed(req.getNewsletterSubscribed());
        }

        userRepository.save(user);
        return mapToUserDetailDTO(user);
    }

    public void changePassword(String userId, ChangePasswordRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        changeUserPassword(user, req);
    }

    public void changePasswordByEmail(String email, ChangePasswordRequest req) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        changeUserPassword(user, req);
    }

    private void changeUserPassword(User user, ChangePasswordRequest req) {
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    public void addLoyaltyPoints(String userId, int points) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setLoyaltyPoints(user.getLoyaltyPoints() + points);
        updateTier(user);
        userRepository.save(user);
    }

    private void updateTier(User user) {
        int points = user.getLoyaltyPoints();
        if (points >= 1000) {
            user.setTier(Tier.GOLD);
        } else if (points >= 500) {
            user.setTier(Tier.SILVER);
        } else {
            user.setTier(Tier.BRONZE);
        }
    }

    private UserDetailDTO mapToUserDetailDTO(User user) {
        return UserDetailDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .preferredStore(user.getPreferredStore())
                .loyaltyPoints(user.getLoyaltyPoints())
                .tier(user.getTier())
                .newsletterSubscribed(user.getNewsletterSubscribed())
                .build();
    }
}

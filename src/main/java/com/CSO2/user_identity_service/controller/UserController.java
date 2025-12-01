package com.CSO2.user_identity_service.controller;

import com.CSO2.user_identity_service.dto.request.ChangePasswordRequest;
import com.CSO2.user_identity_service.dto.request.UpdateProfileRequest;
import com.CSO2.user_identity_service.dto.response.UserDetailDTO;
import com.CSO2.user_identity_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // Assuming the email is the username in UserDetails, but we need the ID.
        // Wait, UserDetailsServiceImpl returns User with email as username.
        // I need to fetch the user by email to get the ID, or better yet, update
        // UserDetailsServiceImpl to return a CustomUserDetails that has the ID.
        // Or I can just look up by email in the service.
        // Let's look up by email in the service for now, or change the service to
        // accept email.
        // But the plan said "User ID from JWT".
        // If I want User ID from JWT, I should have put it in the JWT claims.
        // Let's check JwtUtil. It puts "subject" as email.
        // So I only have email.
        // I will update UserService to find by email or look up ID from email.
        // Actually, `userService.getProfile(String userId)` expects ID.
        // I should probably change `UserService` to `getProfileByEmail` or similar, or
        // fetch the user by email in the controller.
        // Let's fetch the user by email in the controller using a helper or just change
        // the service method to accept email.
        // Changing service method to accept email seems cleaner given the current
        // setup.
        // However, the plan said "User ID from JWT".
        // If I strictly follow the plan, I should have put ID in JWT.
        // But I didn't change JwtUtil to put ID in JWT (it puts username/email).
        // I'll stick to email for now as it's unique.
        // I'll modify UserService to find by Email or just use the repository to find
        // by email here.
        // Actually, let's just use the email from UserDetails and find the user.
        // I'll add `getProfileByEmail` to UserService.
        // But wait, `UserService` uses `findById`.
        // I'll update `UserService` to use `findByEmail` for `getProfile` or add a new
        // method.
        // Let's add `getProfileByEmail` to UserService.
        // Or better, since I am in the controller, I can just use the email.

        // Let's pause and think.
        // If I change UserService to use email, it deviates slightly from "User ID" but
        // achieves the goal.
        // Or I can look up the user by email in the controller (using UserRepository?
        // No, should use Service).
        // I'll add `getUserByEmail` to UserService.

        // Actually, `UserDetails` in Spring Security usually contains the username
        // (email in my case).
        // So `userDetails.getUsername()` gives me the email.

        return ResponseEntity.ok(userService.getProfileByEmail(userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<java.util.List<UserDetailDTO>> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDetailDTO> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(userService.updateProfileByEmail(userDetails.getUsername(), req));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest req) {
        userService.changePasswordByEmail(userDetails.getUsername(), req);
        return ResponseEntity.ok().build();
    }
}

package com.CSO2.user_identity_service.config;

import com.CSO2.user_identity_service.model.User;
import com.CSO2.user_identity_service.model.enums.Role;
import com.CSO2.user_identity_service.model.enums.Tier;
import com.CSO2.user_identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .email("admin@cso2.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .fullName("Admin User")
                    .role(Role.ADMIN)
                    .tier(Tier.GOLD)
                    .loyaltyPoints(1000)
                    .newsletterSubscribed(true)
                    .build();

            User user = User.builder()
                    .email("user@cso2.com")
                    .passwordHash(passwordEncoder.encode("user123"))
                    .fullName("John Doe")
                    .role(Role.BUYER)
                    .tier(Tier.BRONZE)
                    .loyaltyPoints(0)
                    .newsletterSubscribed(false)
                    .build();

            userRepository.save(admin);
            userRepository.save(user);
            System.out.println("User data seeded!");
        }
    }
}

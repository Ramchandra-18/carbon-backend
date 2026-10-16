
package com.carboncalculator.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.enums.Role;
import com.carboncalculator.backend.repository.AppUserRepository;

@Configuration
public class AdminBootstrap {
	
    @Bean
    CommandLineRunner createInitialAdmin(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${BOOTSTRAP_ADMIN_EMAIL:}") String email,
        	@Value("${BOOTSTRAP_ADMIN_PASSWORD:}") String password) {

        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            if (email.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                    "Set BOOTSTRAP_ADMIN_EMAIL and "
                    + "BOOTSTRAP_ADMIN_PASSWORD to initialize "
                    + "the first administrator.");
            }

            if (password.length() < 12) {
                throw new IllegalStateException(
                    "Bootstrap administrator password must "
                    + "contain at least 12 characters.");
            }

            AppUser admin = new AppUser();
            admin.setFullName("System Administrator");
            admin.setEmail(email.trim().toLowerCase());
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);
        };
    }
}

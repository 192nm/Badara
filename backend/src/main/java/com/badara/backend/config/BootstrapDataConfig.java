package com.badara.backend.config;

import com.badara.backend.domain.AppUser;
import com.badara.backend.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapDataConfig {
    @Bean
    CommandLineRunner seedUser(AppUserRepository appUserRepository, PasswordEncoder encoder) {
        return args -> appUserRepository.findByUsername("admin").orElseGet(() -> {
            AppUser user = new AppUser();
            user.setUsername("admin");
            user.setPasswordHash(encoder.encode("admin1234"));
            user.setRole("ADMIN");
            return appUserRepository.save(user);
        });
    }
}

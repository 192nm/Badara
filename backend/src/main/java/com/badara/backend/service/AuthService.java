package com.badara.backend.service;

import com.badara.backend.auth.JwtTokenService;
import com.badara.backend.domain.AppUser;
import com.badara.backend.dto.AuthDtos;
import com.badara.backend.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthDtos.TokenResponse register(AuthDtos.RegisterRequest request) {
        appUserRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already exists");
        });
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole("USER");
        appUserRepository.save(user);
        return new AuthDtos.TokenResponse(jwtTokenService.generate(user.getUsername(), user.getRole()));
    }

    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return new AuthDtos.TokenResponse(jwtTokenService.generate(user.getUsername(), user.getRole()));
    }

    public AppUser requireUser(String username) {
        return appUserRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("User not found"));
    }
}

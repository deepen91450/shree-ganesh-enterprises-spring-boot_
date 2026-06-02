package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.PasswordResetToken;
import com.shreeganesh.enterprises.entity.User;
import com.shreeganesh.enterprises.repository.PasswordResetTokenRepository;
import com.shreeganesh.enterprises.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;

    // ❗ Constructor Injection solves circular dependency
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public User signup(User u) {
        // ✅ Check for existing email
        if (userRepository.findByEmail(u.getEmail()).isPresent()) {
            throw new IllegalArgumentException("EMAIL_EXISTS");
        }
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        u.setProvider("LOCAL");
        u.setRole("USER");
        u.setEnabled(true);
        return userRepository.save(u);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createOrUpdateOauthUser(String provider, String providerId, String name, String email) {
        Optional<User> existing = userRepository.findByProviderAndProviderId(provider, providerId);
        if (existing.isPresent()) return existing.get();

        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setProvider(provider);
        u.setProviderId(providerId);

        // ⭐ IMPORTANT
        u.setRole("USER");
        return userRepository.save(u);
    }

    // ===================== PASSWORD RESET =====================

    @Transactional
    public PasswordResetToken createPasswordResetToken(String email) {

        // 🔒 remove old tokens (requires transaction)
        tokenRepository.deleteByEmail(email);

        PasswordResetToken t = new PasswordResetToken();
        t.setEmail(email);
        t.setToken(UUID.randomUUID().toString());
        t.setExpiresAt(LocalDateTime.now().plusHours(2));

        return tokenRepository.save(t);
    }

    public Optional<PasswordResetToken> findToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void updatePassword(String email, String rawPassword) {
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(u);
        });
    }

    @Transactional
    public void invalidateToken(String token) {
        tokenRepository.findByToken(token)
                .ifPresent(tokenRepository::delete);
    }

    // ===================== PROFILE =====================

    public void updateProfile(String email, User updated) {

        userRepository.findByEmail(email).ifPresent(user -> {
            user.setName(updated.getName());
            user.setPhone(updated.getPhone());
            user.setAddress(updated.getAddress());
            userRepository.save(user);
        });
    }

    // ===================== SAVE USER =====================
    public User save(User user) {
        return userRepository.save(user);
    }
}

package com.pawpal.auth.service;

import com.pawpal.auth.model.Session;
import com.pawpal.auth.model.User;
import com.pawpal.auth.repository.SessionRepository;
import com.pawpal.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private SessionRepository sessionRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Register
    public User register(String name, String email, String password, String role) {
        if (userRepo.findByEmail(email).isPresent()) return null;
        String hash = encoder.encode(password);
        return userRepo.save(new User(name, email, hash, role));
    }

    // Login — returns { token, userId, role } so callers know who logged in
    public Map<String, Object> login(String email, String password) {
        Optional<User> found = userRepo.findByEmail(email);
        if (found.isEmpty()) return null;

        User user = found.get();
        if (!encoder.matches(password, user.getPasswordHash())) return null;

        // Deactivate any existing session
        sessionRepo.findByUserAndIsActiveTrue(user)
            .ifPresent(s -> { s.setActive(false); sessionRepo.save(s); });

        // Create new session
        String token = UUID.randomUUID().toString();
        sessionRepo.save(new Session(user, token));
        return Map.of("token", token, "userId", user.getId(), "role", user.getRole());
    }

    // Logout
    public boolean logout(String token) {
        Optional<Session> session = sessionRepo.findByTokenAndIsActiveTrue(token);
        if (session.isEmpty()) return false;
        session.get().setActive(false);
        sessionRepo.save(session.get());
        return true;
    }

    // Validate token
    public boolean validateToken(String token) {
        return sessionRepo.findByTokenAndIsActiveTrue(token).isPresent();
    }

    // Request password reset
    public String requestReset(String email) {
        Optional<User> found = userRepo.findByEmail(email);
        if (found.isEmpty()) return null;
        String resetToken = UUID.randomUUID().toString();
        User user = found.get();
        user.setResetToken(resetToken);
        userRepo.save(user);
        return resetToken;
    }

    // Confirm password reset
    public boolean confirmReset(String resetToken, String newPassword) {
        Optional<User> found = userRepo.findByResetToken(resetToken);
        if (found.isEmpty()) return false;
        User user = found.get();
        user.setPasswordHash(encoder.encode(newPassword));
        user.setResetToken(null);
        userRepo.save(user);
        return true;
    }
}
package com.pawpal.auth.rest;

import com.pawpal.auth.model.User;
import com.pawpal.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/users")
    public ResponseEntity<Object> register(@RequestBody Map<String, String> body) {
        User user = authService.register(
            body.get("name"),
            body.get("email"),
            body.get("password"),
            body.get("role")
        );
        if (user == null)
            return ResponseEntity.status(409).body("Email already registered");
        return ResponseEntity.status(201).body(user);
    }

    @PostMapping("/sessions")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> body) {
        var result = authService.login(body.get("email"), body.get("password"));
        if (result == null)
            return ResponseEntity.status(401).body("Invalid email or password");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/sessions/{token}")
    public ResponseEntity<Object> logout(@PathVariable String token) {
        boolean success = authService.logout(token);
        if (!success)
            return ResponseEntity.status(404).body("Session not found");
        return ResponseEntity.ok("Logged out");
    }

    @PostMapping("/validations")
    public ResponseEntity<Object> validateToken(@RequestBody Map<String, String> body) {
        boolean valid = authService.validateToken(body.get("token"));
        if (!valid)
            return ResponseEntity.status(401).body(false);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/password-resets")
    public ResponseEntity<Object> requestReset(@RequestBody Map<String, String> body) {
        String resetToken = authService.requestReset(body.get("email"));
        if (resetToken == null)
            return ResponseEntity.status(404).body("Email not found");
        return ResponseEntity.ok(resetToken);
    }

    @PutMapping("/password-resets/{token}")
    public ResponseEntity<Object> confirmReset(@PathVariable String token,
                                          @RequestBody Map<String, String> body) {
        boolean success = authService.confirmReset(token, body.get("newPassword"));
        if (!success)
            return ResponseEntity.status(400).body("Invalid reset token");
        return ResponseEntity.ok("Password reset successful");
    }
}
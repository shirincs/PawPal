package com.pawpal.auth.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class AuthClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    // Constructor 
    public AuthClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    // Register a new user
    public String register(String name, String email, String password, String role) {
        String url = baseUrl + "/auth/users";
        Map<String, String> body = Map.of(
            "name", name,
            "email", email,
            "password", password,
            "role", role
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    // Login - returns token
    public String login(String email, String password) {
        String url = baseUrl + "/auth/sessions";
        Map<String, String> body = Map.of("email", email, "password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    // Logout
    public void logout(String token) {
        String url = baseUrl + "/auth/sessions/" + token;
        restTemplate.delete(url);
    }

    // Validate token
    public boolean validateToken(String token) {
        String url = baseUrl + "/auth/validations";
        Map<String, String> body = Map.of("token", token);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
        return Boolean.TRUE.equals(response.getBody());
    }

    // Request password reset
    public String requestPasswordReset(String email) {
        String url = baseUrl + "/auth/password-resets";
        Map<String, String> body = Map.of("email", email);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    // Confirm password reset
    public String confirmPasswordReset(String resetToken, String newPassword) {
        String url = baseUrl + "/auth/password-resets/" + resetToken;
        Map<String, String> body = Map.of("newPassword", newPassword);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        return response.getBody();
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
package com.pawpal.review.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * CLIENT version - calls Auth Service endpoints
 * (NOT the server-side validation version)
 */
public class AuthClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    // Constructor that takes URL - THIS IS WHAT YOU NEED
    public AuthClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

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

    public String login(String email, String password) {
        String url = baseUrl + "/auth/sessions";
        Map<String, String> body = Map.of("email", email, "password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    public void logout(String token) {
        String url = baseUrl + "/auth/sessions/" + token;
        restTemplate.delete(url);
    }

    public boolean validateToken(String token) {
        String url = baseUrl + "/auth/validations";
        Map<String, String> body = Map.of("token", token);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
        return Boolean.TRUE.equals(response.getBody());
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
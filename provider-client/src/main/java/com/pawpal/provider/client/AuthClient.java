package com.pawpal.provider.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * HTTP client for communicating with the Auth microservice.
 * Used by ProviderClientDemo to register and log in provider accounts.
 */
public class AuthClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

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
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, jsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    // Login — returns session token
    public String login(String email, String password) {
        String url = baseUrl + "/auth/sessions";
        Map<String, String> body = Map.of("email", email, "password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, jsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    // Logout
    public void logout(String token) {
        String url = baseUrl + "/auth/sessions/" + token;
        restTemplate.delete(url);
    }

    // Validate token — returns true if active
    public boolean validateToken(String token) {
        try {
            String url = baseUrl + "/auth/validations";
            Map<String, String> body = Map.of("token", token);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, jsonHeaders());
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (HttpClientErrorException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

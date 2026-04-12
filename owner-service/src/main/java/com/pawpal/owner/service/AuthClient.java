package com.pawpal.owner.service;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AuthClient {

    private final String baseUrl = "http://localhost:8080";
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean validateToken(String token) {
        try {
            String url = baseUrl + "/auth/validations";
            Map<String, String> body = Map.of("token", token);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
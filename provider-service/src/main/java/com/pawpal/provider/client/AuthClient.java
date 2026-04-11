package com.pawpal.provider.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * HTTP client for communicating with the Auth microservice.
 * Used to validate bearer tokens on every incoming provider request.
 */
@Component
public class AuthClient {

    @Value("${auth.service.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Validates a session token against the Auth service.
     * Returns true if the token is active, false if invalid or expired.
     */
    public boolean validateToken(String token) {
        try {
            String url = baseUrl + "/auth/validations";
            Map<String, String> body = Map.of("token", token);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, jsonHeaders());
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (HttpClientErrorException e) {
            // 401 from auth service means token is invalid
            return false;
        } catch (Exception e) {
            // Auth service unreachable — fail closed (deny access)
            return false;
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

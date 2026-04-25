package com.pawpal.owner.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class OwnerClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public OwnerClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public String createOwner(String name, String email) {
        String url = baseUrl + "/owners";
        Map<String, String> body = Map.of(
            "name", name,
            "email", email
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    public String getOwner(int ownerId) {
        String url = baseUrl + "/owners/" + ownerId;
        return restTemplate.getForObject(url, String.class);
    }

    public String updateOwner(int ownerId, String name, String email) {
        String url = baseUrl + "/owners/" + ownerId;
        Map<String, String> body = Map.of(
            "name", name,
            "email", email
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        return response.getBody();
    }

    public String addPet(int ownerId, String name, String type, String breed, int age, String health) {
        String url = baseUrl + "/owners/" + ownerId + "/pets";
        Map<String, Object> body = Map.of(
            "name", name,
            "type", type,
            "breed", breed,
            "age", age,
            "health", health
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    public String getPets(int ownerId) {
        String url = baseUrl + "/owners/" + ownerId + "/pets";
        return restTemplate.getForObject(url, String.class);
    }

    public String addDocument(int ownerId, int petId, String fileName, String documentType, String urlValue) {
        String url = baseUrl + "/owners/" + ownerId + "/pets/" + petId + "/documents";
        Map<String, String> body = Map.of(
            "fileName", fileName,
            "documentType", documentType,
            "url", urlValue
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getJsonHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

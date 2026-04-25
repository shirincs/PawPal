package com.pawpal.review.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Calls Provider Service internal endpoints to push updated average ratings
 * after a new review is submitted.
 */
@Component
public class ProviderClient {

    @Value("${provider.service.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Push a new average rating to a service profile.
     * Maps to PATCH /internal/services/{sid}/rating
     */
    public boolean updateServiceRating(int serviceId, double newAvgRating) {
        System.out.println("🔷 ProviderClient.updateServiceRating CALLED");
        System.out.println("🔷 baseUrl: " + baseUrl);
        System.out.println("🔷 Full URL: " + baseUrl + "/internal/services/" + serviceId + "/rating");
        try {
            String url = baseUrl + "/internal/services/" + serviceId + "/rating";
            Map<String, Double> body = Map.of("avgRating", newAvgRating);
            HttpEntity<Map<String, Double>> request = new HttpEntity<>(body, jsonHeaders());
            restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            System.out.println("🔷 SUCCESS!");
            return true;
        } catch (Exception e) {
            System.out.println("🔷 FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Push a new average rating to a veterinarian profile.
     * Maps to PATCH /internal/veterinarians/{vid}/rating
     */
    public boolean updateVetRating(int vetId, double newAvgRating) throws HttpClientErrorException {
        try {
            String url = baseUrl + "/internal/veterinarians/" + vetId + "/rating";
            Map<String, Double> body = Map.of("avgRating", newAvgRating);
            HttpEntity<Map<String, Double>> request = new HttpEntity<>(body, jsonHeaders());
            restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            return true;
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

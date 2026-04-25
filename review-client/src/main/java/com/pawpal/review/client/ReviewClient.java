package com.pawpal.review.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * HTTP client for communicating with the Review microservice.
 *
 * Used by:
 *   - Pet Owner → submitServiceReview(), submitVetReview(), getServiceReviews(), getVetReviews()
 */
public class ReviewClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public ReviewClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Submit a review for a service.
     * Maps to POST /reviews/services/{sid}
     */
    public String submitServiceReview(String token, int serviceId, int petOwnerId,
                                      int bookingId, int rating, String comment) {
        String url = baseUrl + "/reviews/services/" + serviceId;
        Map<String, String> body = Map.of(
            "petOwnerId", String.valueOf(petOwnerId),
            "bookingId",  String.valueOf(bookingId),
            "rating",     String.valueOf(rating),
            "comment",    comment != null ? comment : ""
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, bearerHeaders(token));
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error " + e.getStatusCode() + ": " + e.getResponseBodyAsString();
        }
    }

    /**
     * Get all reviews and average rating for a service.
     * Maps to GET /reviews/services/{sid}
     */
    public String getServiceReviews(int serviceId) {
        String url = baseUrl + "/reviews/services/" + serviceId;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error " + e.getStatusCode() + ": " + e.getResponseBodyAsString();
        }
    }

    /**
     * Submit a review for a veterinarian.
     * Maps to POST /reviews/veterinarians/{vid}
     */
    public String submitVetReview(String token, int vetId, int petOwnerId,
                                  int bookingId, int rating, String comment) {
        String url = baseUrl + "/reviews/veterinarians/" + vetId;
        Map<String, String> body = Map.of(
            "petOwnerId", String.valueOf(petOwnerId),
            "bookingId",  String.valueOf(bookingId),
            "rating",     String.valueOf(rating),
            "comment",    comment != null ? comment : ""
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, bearerHeaders(token));
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error " + e.getStatusCode() + ": " + e.getResponseBodyAsString();
        }
    }

    /**
     * Get all reviews and average rating for a veterinarian.
     * Maps to GET /reviews/veterinarians/{vid}
     */
    public String getVetReviews(int vetId) {
        String url = baseUrl + "/reviews/veterinarians/" + vetId;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error " + e.getStatusCode() + ": " + e.getResponseBodyAsString();
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}

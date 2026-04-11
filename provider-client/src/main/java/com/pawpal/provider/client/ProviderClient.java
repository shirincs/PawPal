package com.pawpal.provider.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * HTTP client for communicating with the Provider microservice.
 *
 * Used by:
 *   - Pet Owner  → createBooking(), getMyBookings(), cancelBooking()
 *   - Provider   → getBookingsForService(), confirmBooking(), cancelBooking()
 *   - Review Service → updateServiceRating(), updateVetRating()
 */
public class ProviderClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public ProviderClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    // -------------------------------------------------------------------------
    // Public service queries
    // -------------------------------------------------------------------------

    /**
     * Search/filter services by service type, location, and minimum rating.
     * Maps to GET /services
     */
    public String searchServices(String service, String location, Double minRating) {
        StringBuilder url = new StringBuilder(baseUrl + "/services?");
        if (service != null)   url.append("service=").append(service).append("&");
        if (location != null)  url.append("location=").append(location).append("&");
        if (minRating != null) url.append("rating=").append(minRating);

        ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
        return response.getBody();
    }

    /**
     * Get a full service profile by its ID (includes vets and avgRating).
     * Maps to GET /services/{sid}
     */
    public String getServiceById(int serviceId) {
        String url = baseUrl + "/services/" + serviceId;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    /**
     * Get a veterinarian profile and rating.
     * Maps to GET /veterinarians/{vid}
     */
    public String getVetById(int vetId) {
        String url = baseUrl + "/veterinarians/" + vetId;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Booking endpoints
    // -------------------------------------------------------------------------

    /**
     * Pet owner creates a booking with a provider.
     * Maps to POST /services/{sid}/bookings
     */
    public String createBooking(String token, int serviceId, int petOwnerId, int petId,
                                String date, String time, String serviceType, String notes) {
        String url = baseUrl + "/services/" + serviceId + "/bookings";
        Map<String, String> body = Map.of(
            "petOwnerId",  String.valueOf(petOwnerId),
            "petId",       String.valueOf(petId),
            "date",        date,
            "time",        time,
            "serviceType", serviceType != null ? serviceType : "",
            "notes",       notes != null ? notes : ""
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, bearerHeaders(token));
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    /**
     * Provider views bookings for their service.
     * Maps to GET /services/{sid}/bookings
     */
    public String getBookingsForService(String token, int serviceId, String date, String status) {
        StringBuilder url = new StringBuilder(baseUrl + "/services/" + serviceId + "/bookings?");
        if (date != null)   url.append("date=").append(date).append("&");
        if (status != null) url.append("status=").append(status);

        HttpEntity<Void> request = new HttpEntity<>(bearerHeaders(token));
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url.toString(), HttpMethod.GET, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    /**
     * Pet owner views all their own bookings.
     * Maps to GET /bookings?petOwnerId={id}
     */
    public String getMyBookings(String token, int petOwnerId) {
        String url = baseUrl + "/bookings?petOwnerId=" + petOwnerId;
        HttpEntity<Void> request = new HttpEntity<>(bearerHeaders(token));
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    /**
     * Provider confirms a pending booking.
     * Maps to PATCH /services/{sid}/bookings/{bid}/confirm
     */
    public String confirmBooking(String token, int serviceId, int bookingId) {
        String url = baseUrl + "/services/" + serviceId + "/bookings/" + bookingId + "/confirm";
        HttpEntity<Void> request = new HttpEntity<>(bearerHeaders(token));
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.PATCH, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    /**
     * Cancel a booking — provider or pet owner.
     * Maps to PATCH /services/{sid}/bookings/{bid}/cancel
     */
    public String cancelBooking(String token, int serviceId, int bookingId) {
        String url = baseUrl + "/services/" + serviceId + "/bookings/" + bookingId + "/cancel";
        HttpEntity<Void> request = new HttpEntity<>(bearerHeaders(token));
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.PATCH, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Authenticated provider management calls
    // -------------------------------------------------------------------------

    /**
     * Register a new provider service profile.
     * Maps to POST /services
     */
    public String createService(String token, String name, String address, String location,
                                String operatingHours, String services, String pricing, int userId) {
        String url = baseUrl + "/services";
        Map<String, String> body = Map.of(
            "name",           name,
            "address",        address,
            "location",       location,
            "operatingHours", operatingHours,
            "services",       services,
            "pricing",        pricing,
            "userId",         String.valueOf(userId)
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, bearerHeaders(token));
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Internal calls — service-to-service, no token needed
    // -------------------------------------------------------------------------

    /**
     * Push a new average rating to a service profile.
     * Called by Review Service.
     * Maps to PATCH /internal/services/{sid}/rating
     */
    public String updateServiceRating(int serviceId, double newAvgRating) {
        String url = baseUrl + "/internal/services/" + serviceId + "/rating";
        Map<String, Double> body = Map.of("avgRating", newAvgRating);
        HttpEntity<Map<String, Double>> request = new HttpEntity<>(body, jsonHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.PATCH, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    /**
     * Push a new average rating to a veterinarian profile.
     * Called by Review Service.
     * Maps to PATCH /internal/veterinarians/{vid}/rating
     */
    public String updateVetRating(int vetId, double newAvgRating) {
        String url = baseUrl + "/internal/veterinarians/" + vetId + "/rating";
        Map<String, Double> body = Map.of("avgRating", newAvgRating);
        HttpEntity<Map<String, Double>> request = new HttpEntity<>(body, jsonHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.PATCH, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

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

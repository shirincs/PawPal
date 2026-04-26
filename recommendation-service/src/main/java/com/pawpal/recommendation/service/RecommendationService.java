package com.pawpal.recommendation.service;

import com.pawpal.recommendation.model.Recommendation;
import com.pawpal.recommendation.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepo;

    @Autowired
    private RestTemplate restTemplate;

    private String providerServiceUrl = "http://localhost:8082";

    // POST /recommendations
    public List<Map<String, Object>> generateRecommendations(int userId, int petId,
                                                              String service, String requestedTime) {

        // 1. Fetch providers that offer the requested service
        List<Map<String, Object>> providers = fetchProviders(service);

        // 2. If user gave a time, filter to only providers with open slots
        if (requestedTime != null && !requestedTime.isBlank()) {
            providers = filterByAvailability(providers, requestedTime);
        }

        // 3. Sort by avgRating descending
        providers.sort((a, b) -> {
            double ratingA = a.get("avgRating") != null ? ((Number) a.get("avgRating")).doubleValue() : 0;
            double ratingB = b.get("avgRating") != null ? ((Number) b.get("avgRating")).doubleValue() : 0;
            return Double.compare(ratingB, ratingA);
        });

        // 4. Loop through providers — save one row per provider and build response
        List<Map<String, Object>> results = new ArrayList<>();

        for (Map<String, Object> provider : providers) {
            int providerId = provider.get("id") != null ? ((Number) provider.get("id")).intValue() : 0;

            // Save one recommendation row per provider
            Recommendation rec = new Recommendation(userId, providerId, service);
            recommendationRepo.save(rec);

            // Build response entry
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("recommendationId", rec.getRecommendationId());
            entry.put("providerId", providerId);
            entry.put("name", provider.get("name"));
            entry.put("address", provider.get("address"));
            entry.put("score", provider.get("avgRating")); // score = avgRating out of 5
            results.add(entry);
        }

        return results;
    }

    // GET /recommendations/:rid
    public Map<String, Object> getRecommendationById(int id) {
        Optional<Recommendation> found = recommendationRepo.findById(id);
        if (found.isEmpty()) return null;
        Recommendation rec = found.get();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("recommendationId", rec.getRecommendationId());
        response.put("userId", rec.getUserId());
        response.put("providerId", rec.getProviderId());
        response.put("service", rec.getService());
        return response;
    }

    // GET /recommendations/user/:oid
    public List<Map<String, Object>> getRecommendationsByUser(int userId) {
        List<Recommendation> recs = recommendationRepo.findByUserId(userId);
        List<Map<String, Object>> results = new ArrayList<>();

        for (Recommendation rec : recs) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("recommendationId", rec.getRecommendationId());
            entry.put("userId", rec.getUserId());
            entry.put("providerId", rec.getProviderId());
            entry.put("service", rec.getService());
            results.add(entry);
        }

        return results;
    }

    // -----------------------------------------------------------------------
    // Inter-service calls
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchProviders(String service) {
        try {
            String url = providerServiceUrl + "/service?service=" + service;
            Object response = restTemplate.getForObject(url, Object.class);
            if (response instanceof List) {
                return (List<Map<String, Object>>) response;
            }
        } catch (RestClientException e) {
            // Provider service unavailable
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> filterByAvailability(List<Map<String, Object>> providers,
                                                            String requestedTime) {
        List<Map<String, Object>> available = new ArrayList<>();

        for (Map<String, Object> provider : providers) {
            Object providerId = provider.get("id");
            if (providerId == null) continue;
            try {
                String url = providerServiceUrl + "/service/" + providerId
                           + "/availability?date=" + requestedTime;
                Object response = restTemplate.getForObject(url, Object.class);
                if (response instanceof List && !((List<?>) response).isEmpty()) {
                    available.add(provider);
                }
            } catch (RestClientException e) {
                // If availability check fails, include provider anyway
                available.add(provider);
            }
        }

        return available;
    }
}

package com.pawpal.recommendation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recommendationId;

    private int userId;
    private int providerId;
    private String service;

    public Recommendation() {}

    public Recommendation(int userId, int providerId, String service) {
        this.userId = userId;
        this.providerId = providerId;
        this.service = service;
    }

    public int getRecommendationId() { return recommendationId; }
    public void setRecommendationId(int recommendationId) { this.recommendationId = recommendationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProviderId() { return providerId; }
    public void setProviderId(int providerId) { this.providerId = providerId; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
}

package com.pawpal.recommendation.rest;

import com.pawpal.recommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    // POST /recommendations
    @PostMapping
    public ResponseEntity<Object> createRecommendation(@RequestBody Map<String, String> body) {
        String ownerIdStr = body.get("ownerId");
        String petIdStr   = body.get("petId");
        String service    = body.get("service");
        String time       = body.get("time"); // optional

        if (ownerIdStr == null || petIdStr == null || service == null) {
            return ResponseEntity.status(400).body("ownerId, petId, and service are required");
        }

        int ownerId;
        int petId;
        try {
            ownerId = Integer.parseInt(ownerIdStr);
            petId   = Integer.parseInt(petIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("ownerId and petId must be integers");
        }

        List<Map<String, Object>> results = recommendationService.generateRecommendations(
                ownerId, petId, service, time
        );

        return ResponseEntity.status(201).body(results);
    }

    // GET /recommendations/:rid
    @GetMapping("/{rid}")
    public ResponseEntity<Object> getRecommendation(@PathVariable int rid) {
        Map<String, Object> result = recommendationService.getRecommendationById(rid);
        if (result == null)
            return ResponseEntity.status(404).body("Recommendation not found");
        return ResponseEntity.ok(result);
    }

    // GET /recommendations/user/:oid
    @GetMapping("/user/{oid}")
    public ResponseEntity<Object> getRecommendationsByUser(@PathVariable int oid) {
        List<Map<String, Object>> results = recommendationService.getRecommendationsByUser(oid);
        return ResponseEntity.ok(results);
    }
}

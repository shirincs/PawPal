package com.pawpal.review.rest;

import com.pawpal.review.client.AuthClient;
import com.pawpal.review.model.Review;
import com.pawpal.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthClient authClient;

    private String extractAndValidate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return authClient.validateToken(token) ? token : null;
    }

    // -------------------------------------------------------------------------
    // Service reviews  —  /reviews/services/{sid}
    // -------------------------------------------------------------------------

    /**
     * POST /reviews/services/{sid}
     * Submit a rating/review for a service.
     * Access: Authenticated (Pet Owner)
     * Body: { petOwnerId, bookingId, rating, comment }
     */
    @PostMapping("/reviews/services/{sid}")
    public ResponseEntity<Object> submitServiceReview(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int sid,
            @RequestBody Map<String, String> body) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        int petOwnerId;
        int bookingId;
        int rating;
        try {
            petOwnerId = Integer.parseInt(body.get("petOwnerId"));
            bookingId  = Integer.parseInt(body.get("bookingId"));
            rating     = Integer.parseInt(body.get("rating"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("petOwnerId, bookingId, and rating must be valid integers");
        }

        Review review = reviewService.submitReview("SERVICE", sid, petOwnerId, bookingId, rating, body.get("comment"));

        if (review == null)
            return ResponseEntity.status(409).body("A review for this booking already exists, or rating is out of range (1-5)");

        return ResponseEntity.status(201).body(review);
    }

    /**
     * GET /reviews/services/{sid}
     * Get all reviews and average rating for a service.
     * Access: Public
     */
    @GetMapping("/reviews/services/{sid}")
    public ResponseEntity<Object> getServiceReviews(@PathVariable int sid) {
        Map<String, Object> result = reviewService.getReviews("SERVICE", sid);
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // Veterinarian reviews  —  /reviews/veterinarians/{vid}
    // -------------------------------------------------------------------------

    /**
     * POST /reviews/veterinarians/{vid}
     * Submit a rating/review for a veterinarian.
     * Access: Authenticated (Pet Owner)
     * Body: { petOwnerId, bookingId, rating, comment }
     */
    @PostMapping("/reviews/veterinarians/{vid}")
    public ResponseEntity<Object> submitVetReview(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int vid,
            @RequestBody Map<String, String> body) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        int petOwnerId;
        int bookingId;
        int rating;
        try {
            petOwnerId = Integer.parseInt(body.get("petOwnerId"));
            bookingId  = Integer.parseInt(body.get("bookingId"));
            rating     = Integer.parseInt(body.get("rating"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("petOwnerId, bookingId, and rating must be valid integers");
        }

        Review review = reviewService.submitReview("VETERINARIAN", vid, petOwnerId, bookingId, rating, body.get("comment"));

        if (review == null)
            return ResponseEntity.status(409).body("A review for this booking already exists, or rating is out of range (1-5)");

        return ResponseEntity.status(201).body(review);
    }

    /**
     * GET /reviews/veterinarians/{vid}
     * Get all reviews and average rating for a veterinarian.
     * Access: Public
     */
    @GetMapping("/reviews/veterinarians/{vid}")
    public ResponseEntity<Object> getVetReviews(@PathVariable int vid) {
        Map<String, Object> result = reviewService.getReviews("VETERINARIAN", vid);
        return ResponseEntity.ok(result);
    }
}

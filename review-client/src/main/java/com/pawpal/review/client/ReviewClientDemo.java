package com.pawpal.review.client;

/**
 * End-to-end demo of the Review microservice via ReviewClient.
 *
 * Prerequisites:
 *   1. Auth service running on localhost:8080
 *   2. Provider service running on localhost:8082
 *   3. Review service running on localhost:8083
 *
 * This demo assumes a booking already exists (bookingId=1, serviceId=1).
 * Run ProviderClientDemo first to create the booking, then run this.
 */
public class ReviewClientDemo {

    public static void main(String[] args) {

        AuthClient authClient  = new AuthClient("http://localhost:8080");
        ReviewClient client    = new ReviewClient("http://localhost:8083");

        String timestamp = String.valueOf(System.currentTimeMillis());

        System.out.println("========================================");
        System.out.println("  PawPal Review Service Demo");
        System.out.println("========================================\n");

        // 1. Register a pet owner and log in
        System.out.println("1. Registering pet owner...");
        String ownerEmail = "reviewer_" + timestamp + "@pawpal.com";
        String registerResponse = authClient.register("Alyah Ahmed", ownerEmail, "pass123", "PET_OWNER");
        System.out.println("   Response: " + registerResponse);

        System.out.println("\n2. Logging in as pet owner...");
        String loginResponse = authClient.login(ownerEmail, "pass123");
        System.out.println("   Login response: " + loginResponse);

        // Parse token from login response — format: {"token":"...","userId":...,"role":"..."}
        // For demo purposes, replace with actual token from login response
        String token = parseToken(loginResponse);
        System.out.println("   Token: " + token);

        int petOwnerId = 1; // Replace with actual userId from register response
        int serviceId  = 1; // Replace with actual serviceId from ProviderClientDemo
        int bookingId  = 1; // Replace with actual bookingId from ProviderClientDemo

        // 3. Submit a review for the service
        System.out.println("\n3. Submitting review for service " + serviceId + "...");
        String reviewResponse = client.submitServiceReview(
            token, serviceId, petOwnerId, bookingId, 5,
            "Excellent service! The staff was very professional and my dog loved it."
        );
        System.out.println("   Response: " + reviewResponse);

        // 4. Get all reviews for the service
        System.out.println("\n4. Getting reviews for service " + serviceId + "...");
        System.out.println("   Reviews: " + client.getServiceReviews(serviceId));

        // 5. Try to submit another review for the same booking (should return 409)
        System.out.println("\n5. Submitting duplicate review for same booking (expect 409)...");
        String duplicateResponse = client.submitServiceReview(
            token, serviceId, petOwnerId, bookingId, 3, "Trying again"
        );
        System.out.println("   Response: " + duplicateResponse);

        // 6. Try a rating out of range (should return 409)
        System.out.println("\n6. Submitting invalid rating=6 for a new booking (expect 409)...");
        String invalidRating = client.submitServiceReview(
            token, serviceId, petOwnerId, 999, 6, "Bad rating"
        );
        System.out.println("   Response: " + invalidRating);

        // 7. Logout
        System.out.println("\n7. Logging out...");
        authClient.logout(token);
        System.out.println("   Logged out.");

        System.out.println("\n========================================");
        System.out.println("  All demo steps done!");
        System.out.println("========================================");
    }

    // Simple token parser — extracts token from {"token":"abc","userId":1,"role":"PET_OWNER"}
    private static String parseToken(String loginResponse) {
        if (loginResponse == null) return "";
        try {
            int start = loginResponse.indexOf("\"token\":\"") + 9;
            int end   = loginResponse.indexOf("\"", start);
            return loginResponse.substring(start, end);
        } catch (Exception e) {
            return loginResponse; // fallback: return raw response
        }
    }
}

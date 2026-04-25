package com.pawpal.review.service;

import com.pawpal.review.client.ProviderClient;
import com.pawpal.review.model.Review;
import com.pawpal.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ProviderClient providerClient;

    /**
     * Submit a review for a service or veterinarian.
     * Returns null if booking already has a review.
     * Returns the saved review and pushes updated avg rating to provider service.
     * 
     * 
     */
    
    public Review submitReview(String targetType, int targetId, int petOwnerId,
            int bookingId, int rating, String comment) {
System.out.println("1️⃣ submitReview STARTED for " + targetType + " ID: " + targetId);

if (reviewRepo.existsByBookingId(bookingId)) {
System.out.println("2️⃣ Booking already has a review, returning null");
return null;
}
if (rating < 1 || rating > 5) {
System.out.println("3️⃣ Invalid rating: " + rating);
return null;
}

Review review = reviewRepo.save(
new Review(targetType, targetId, petOwnerId, bookingId, rating, comment)
);
System.out.println("4️⃣ Review saved with ID: " + review.getId());

// Recalculate and push avg rating to provider service
System.out.println("5️⃣ Calling pushUpdatedRating...");
pushUpdatedRating(targetType, targetId);
System.out.println("6️⃣ pushUpdatedRating finished");

return review;
}

private void pushUpdatedRating(String targetType, int targetId) {
System.out.println("7️⃣ pushUpdatedRating called for " + targetType + " ID: " + targetId);

List<Review> all = reviewRepo.findByTargetTypeAndTargetId(targetType, targetId);
System.out.println("8️⃣ Found " + all.size() + " reviews");

OptionalDouble avg = all.stream().mapToInt(Review::getRating).average();
double newAvg = avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;
System.out.println("9️⃣ New average: " + newAvg);

if ("SERVICE".equals(targetType)) {
System.out.println("🔟 Calling providerClient.updateServiceRating...");
boolean result = providerClient.updateServiceRating(targetId, newAvg);
System.out.println("1️⃣1️⃣ Result: " + result);
} else if ("VETERINARIAN".equals(targetType)) {
System.out.println("🔟 Calling providerClient.updateVetRating...");
boolean result = providerClient.updateVetRating(targetId, newAvg);
System.out.println("1️⃣1️⃣ Result: " + result);
}
}
    
    

    /**
     * Get all reviews for a target (service or vet) plus the average rating.
     */
    public Map<String, Object> getReviews(String targetType, int targetId) {
        List<Review> reviews = reviewRepo.findByTargetTypeAndTargetId(targetType, targetId);
        OptionalDouble avg = reviews.stream()
            .mapToInt(Review::getRating)
            .average();
        double avgRating = avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;
        return Map.of("avgRating", avgRating, "reviews", reviews);
    }

   
    
}

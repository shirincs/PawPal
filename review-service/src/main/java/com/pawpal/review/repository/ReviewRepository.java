package com.pawpal.review.repository;

import com.pawpal.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByTargetTypeAndTargetId(String targetType, int targetId);
    Optional<Review> findByBookingId(int bookingId);
    boolean existsByBookingId(int bookingId);
}

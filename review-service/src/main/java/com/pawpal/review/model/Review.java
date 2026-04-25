package com.pawpal.review.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"booking_id"})  // one review per booking
})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Who is being reviewed
    @Column(nullable = false)
    private String targetType; // "SERVICE" or "VETERINARIAN"

    @Column(nullable = false)
    private int targetId; // serviceId or vetId

    // Who is reviewing
    @Column(nullable = false)
    private int petOwnerId; // userId from Auth service

    // Which booking this review is for (one review per booking)
    @Column(name = "booking_id", nullable = false)
    private int bookingId;

    @Column(nullable = false)
    private int rating; // 1-5

    private String comment;

    private LocalDateTime createdAt;

    public Review() {}

    public Review(String targetType, int targetId, int petOwnerId, int bookingId, int rating, String comment) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.petOwnerId = petOwnerId;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public int getTargetId() { return targetId; }
    public void setTargetId(int targetId) { this.targetId = targetId; }

    public int getPetOwnerId() { return petOwnerId; }
    public void setPetOwnerId(int petOwnerId) { this.petOwnerId = petOwnerId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

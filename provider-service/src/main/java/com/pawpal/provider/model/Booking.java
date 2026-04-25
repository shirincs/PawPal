package com.pawpal.provider.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"provider_id", "pet_id", "date"})
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private int petOwnerId;  // userId from Auth service

    @Column(nullable = false)
    private int petId;       // which pet is being booked

    private LocalDate date;
    private LocalTime time;

    private String serviceType; // e.g. "grooming", "vet", "daycare"
    private String notes;       // optional notes from pet owner

    // Status: PENDING, CONFIRMED, CANCELLED
    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;

    public Booking() {}

    public Booking(Provider provider, int petOwnerId, int petId, LocalDate date, LocalTime time,
                   String serviceType, String notes) {
        this.provider = provider;
        this.petOwnerId = petOwnerId;
        this.petId = petId;
        this.date = date;
        this.time = time;
        this.serviceType = serviceType;
        this.notes = notes;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public int getPetOwnerId() { return petOwnerId; }
    public void setPetOwnerId(int petOwnerId) { this.petOwnerId = petOwnerId; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

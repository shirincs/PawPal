package com.pawpal.provider.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "providers")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String address;
    private String location;       // e.g. "Dubai" or lat/lng string
    private String operatingHours; // e.g. "Mon-Fri 9am-5pm"
    private String services;       // comma-separated: "grooming,vet,daycare"
    private String pricing;        // e.g. "AED 100-500"

    @Column(nullable = false)
    private double avgRating = 0.0;

    // userId from the Auth service — links this profile to the registered provider account
    @Column(unique = true, nullable = false)
    private int userId;

    @JsonIgnore
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderVet> providerVets = new ArrayList<>();

    public Provider() {}

    public Provider(String name, String address, String location,
                    String operatingHours, String services, String pricing, int userId) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.operatingHours = operatingHours;
        this.services = services;
        this.pricing = pricing;
        this.userId = userId;
        this.avgRating = 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }

    public String getPricing() { return pricing; }
    public void setPricing(String pricing) { this.pricing = pricing; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public List<ProviderVet> getProviderVets() { return providerVets; }
    public void setProviderVets(List<ProviderVet> providerVets) { this.providerVets = providerVets; }
}

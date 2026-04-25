package com.pawpal.provider.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veterinarians")
public class Veterinarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String specialization;

    @Column(nullable = false)
    private double avgRating = 0.0;

    @JsonIgnore
    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderVet> providerVets = new ArrayList<>();

    public Veterinarian() {}

    public Veterinarian(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
        this.avgRating = 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public List<ProviderVet> getProviderVets() { return providerVets; }
    public void setProviderVets(List<ProviderVet> providerVets) { this.providerVets = providerVets; }
}

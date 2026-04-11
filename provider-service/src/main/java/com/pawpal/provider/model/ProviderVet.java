package com.pawpal.provider.model;

import jakarta.persistence.*;

@Entity
@Table(name = "provider_vets")
public class ProviderVet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "vet_id", nullable = false)
    private Veterinarian veterinarian;

    public ProviderVet() {}

    public ProviderVet(Provider provider, Veterinarian veterinarian) {
        this.provider = provider;
        this.veterinarian = veterinarian;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public Veterinarian getVeterinarian() { return veterinarian; }
    public void setVeterinarian(Veterinarian veterinarian) { this.veterinarian = veterinarian; }
}

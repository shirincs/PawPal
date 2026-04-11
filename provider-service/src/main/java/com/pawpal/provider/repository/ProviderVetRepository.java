package com.pawpal.provider.repository;

import com.pawpal.provider.model.Provider;
import com.pawpal.provider.model.ProviderVet;
import com.pawpal.provider.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderVetRepository extends JpaRepository<ProviderVet, Integer> {
    List<ProviderVet> findByProvider(Provider provider);
    Optional<ProviderVet> findByProviderAndVeterinarian(Provider provider, Veterinarian veterinarian);
    boolean existsByProviderAndVeterinarian(Provider provider, Veterinarian veterinarian);
}

package com.pawpal.provider.repository;

import com.pawpal.provider.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    Optional<Provider> findByUserId(int userId);
    List<Provider> findByLocationContainingIgnoreCase(String location);
    List<Provider> findByServicesContainingIgnoreCase(String service);
    List<Provider> findByAvgRatingGreaterThanEqual(double minRating);
}

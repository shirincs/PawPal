package com.pawpal.provider.repository;

import com.pawpal.provider.model.Booking;
import com.pawpal.provider.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByProvider(Provider provider);
    List<Booking> findByPetOwnerId(int petOwnerId);
    List<Booking> findByProviderAndDate(Provider provider, LocalDate date);
    List<Booking> findByProviderAndStatus(Provider provider, String status);
    List<Booking> findByPetOwnerIdAndStatus(int petOwnerId, String status);
    boolean existsByProviderAndPetIdAndDate(Provider provider, int petId, LocalDate date);
}

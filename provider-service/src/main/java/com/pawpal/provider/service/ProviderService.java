package com.pawpal.provider.service;

import com.pawpal.provider.model.Booking;
import com.pawpal.provider.model.Provider;
import com.pawpal.provider.model.ProviderVet;
import com.pawpal.provider.model.Veterinarian;
import com.pawpal.provider.repository.BookingRepository;
import com.pawpal.provider.repository.ProviderRepository;
import com.pawpal.provider.repository.ProviderVetRepository;
import com.pawpal.provider.repository.VeterinarianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepo;

    @Autowired
    private VeterinarianRepository vetRepo;

    @Autowired
    private ProviderVetRepository providerVetRepo;

    @Autowired
    private BookingRepository bookingRepo;

    // -------------------------------------------------------------------------
    // Provider CRUD
    // -------------------------------------------------------------------------

    public Provider createProvider(String name, String address, String location,
                                   String operatingHours, String services,
                                   String pricing, int userId) {
        if (providerRepo.findByUserId(userId).isPresent()) return null;
        return providerRepo.save(new Provider(name, address, location, operatingHours, services, pricing, userId));
    }

    public Optional<Provider> getProviderById(int id) {
        return providerRepo.findById(id);
    }

    public Optional<Provider> getProviderByUserId(int userId) {
        return providerRepo.findByUserId(userId);
    }

    public List<Provider> searchProviders(String service, String location, Double minRating) {
        return providerRepo.findAll().stream()
            .filter(p -> service == null || (p.getServices() != null &&
                p.getServices().toLowerCase().contains(service.toLowerCase())))
            .filter(p -> location == null || (p.getLocation() != null &&
                p.getLocation().toLowerCase().contains(location.toLowerCase())))
            .filter(p -> minRating == null || p.getAvgRating() >= minRating)
            .toList();
    }

    public Provider updateProvider(int id, Map<String, String> fields) {
        Optional<Provider> found = providerRepo.findById(id);
        if (found.isEmpty()) return null;

        Provider p = found.get();
        if (fields.containsKey("name"))           p.setName(fields.get("name"));
        if (fields.containsKey("address"))        p.setAddress(fields.get("address"));
        if (fields.containsKey("location"))       p.setLocation(fields.get("location"));
        if (fields.containsKey("operatingHours")) p.setOperatingHours(fields.get("operatingHours"));
        if (fields.containsKey("services"))       p.setServices(fields.get("services"));
        if (fields.containsKey("pricing"))        p.setPricing(fields.get("pricing"));

        return providerRepo.save(p);
    }

    public Provider updateProviderRating(int providerId, double newAvgRating) {
        Optional<Provider> found = providerRepo.findById(providerId);
        if (found.isEmpty()) return null;
        Provider p = found.get();
        p.setAvgRating(newAvgRating);
        return providerRepo.save(p);
    }

    // -------------------------------------------------------------------------
    // Veterinarian management
    // -------------------------------------------------------------------------

    public Veterinarian addNewVetToProvider(int providerId, String name, String specialization) {
        Optional<Provider> found = providerRepo.findById(providerId);
        if (found.isEmpty()) return null;

        Provider provider = found.get();
        Veterinarian vet = vetRepo.save(new Veterinarian(name, specialization));
        providerVetRepo.save(new ProviderVet(provider, vet));
        return vet;
    }

    public ProviderVet linkExistingVetToProvider(int providerId, int vetId) {
        Optional<Provider> foundProvider = providerRepo.findById(providerId);
        Optional<Veterinarian> foundVet = vetRepo.findById(vetId);
        if (foundProvider.isEmpty() || foundVet.isEmpty()) return null;

        Provider provider = foundProvider.get();
        Veterinarian vet = foundVet.get();

        if (providerVetRepo.existsByProviderAndVeterinarian(provider, vet)) return null;
        return providerVetRepo.save(new ProviderVet(provider, vet));
    }

    public List<Veterinarian> getVetsForProvider(int providerId) {
        Optional<Provider> found = providerRepo.findById(providerId);
        if (found.isEmpty()) return null;
        return providerVetRepo.findByProvider(found.get())
            .stream()
            .map(ProviderVet::getVeterinarian)
            .toList();
    }

    public boolean removeVetFromProvider(int providerId, int vetId) {
        Optional<Provider> foundProvider = providerRepo.findById(providerId);
        Optional<Veterinarian> foundVet = vetRepo.findById(vetId);
        if (foundProvider.isEmpty() || foundVet.isEmpty()) return false;

        Optional<ProviderVet> link = providerVetRepo
            .findByProviderAndVeterinarian(foundProvider.get(), foundVet.get());
        if (link.isEmpty()) return false;

        providerVetRepo.delete(link.get());
        return true;
    }

    public Optional<Veterinarian> getVetById(int vetId) {
        return vetRepo.findById(vetId);
    }

    public Veterinarian updateVetRating(int vetId, double newAvgRating) {
        Optional<Veterinarian> found = vetRepo.findById(vetId);
        if (found.isEmpty()) return null;
        Veterinarian vet = found.get();
        vet.setAvgRating(newAvgRating);
        return vetRepo.save(vet);
    }

    // -------------------------------------------------------------------------
    // Bookings
    // -------------------------------------------------------------------------

    // Pet owner creates a booking with a provider
    // Returns null if provider not found, "duplicate" if that pet is already booked on that day
    public Object createBooking(int providerId, int petOwnerId, int petId, LocalDate date, LocalTime time,
                                String serviceType, String notes) {
        Optional<Provider> found = providerRepo.findById(providerId);
        if (found.isEmpty()) return null;

        Provider provider = found.get();
        if (bookingRepo.existsByProviderAndPetIdAndDate(provider, petId, date))
            return "duplicate";

        return bookingRepo.save(new Booking(provider, petOwnerId, petId, date, time, serviceType, notes));
    }

    // Provider views their bookings — optionally filtered by date or status
    public List<Booking> getBookingsForProvider(int providerId, LocalDate date, String status) {
        Optional<Provider> found = providerRepo.findById(providerId);
        if (found.isEmpty()) return null;
        Provider provider = found.get();

        if (date != null)   return bookingRepo.findByProviderAndDate(provider, date);
        if (status != null) return bookingRepo.findByProviderAndStatus(provider, status);
        return bookingRepo.findByProvider(provider);
    }

    // Pet owner views all their own bookings
    public List<Booking> getBookingsForPetOwner(int petOwnerId) {
        return bookingRepo.findByPetOwnerId(petOwnerId);
    }

    // Provider confirms a pending booking
    public Booking confirmBooking(int bookingId) {
        Optional<Booking> found = bookingRepo.findById(bookingId);
        if (found.isEmpty()) return null;
        Booking booking = found.get();
        if (!"PENDING".equals(booking.getStatus())) return null;
        booking.setStatus("CONFIRMED");
        return bookingRepo.save(booking);
    }

    // Cancel a booking — provider or pet owner
    public Booking cancelBooking(int bookingId) {
        Optional<Booking> found = bookingRepo.findById(bookingId);
        if (found.isEmpty()) return null;
        Booking booking = found.get();
        if ("CANCELLED".equals(booking.getStatus())) return null;
        booking.setStatus("CANCELLED");
        return bookingRepo.save(booking);
    }
}

package com.pawpal.provider.rest;

import com.pawpal.provider.client.AuthClient;
import com.pawpal.provider.model.Booking;
import com.pawpal.provider.model.Provider;
import com.pawpal.provider.model.ProviderVet;
import com.pawpal.provider.model.Veterinarian;
import com.pawpal.provider.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private AuthClient authClient;

    // Helper — extracts and validates the Bearer token from the Authorization header
    private String extractAndValidate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return authClient.validateToken(token) ? token : null;
    }

    // -------------------------------------------------------------------------
    // Provider endpoints  —  /services
    // -------------------------------------------------------------------------

    /**
     * GET /services
     * Search/list providers by optional query params: service, location, rating
     * Access: Public
     */
    @GetMapping("/services")
    public ResponseEntity<Object> listServices(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double rating) {

        List<Provider> results = providerService.searchProviders(service, location, rating);
        return ResponseEntity.ok(results);
    }

    /**
     * POST /services
     * Register a new provider service profile.
     * Access: Authenticated (Provider)
     * Body: { name, address, location, operatingHours, services, pricing, userId }
     */
    @PostMapping("/services")
    public ResponseEntity<Object> createService(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        int userId;
        try {
            userId = Integer.parseInt(body.get("userId"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("userId must be a valid integer");
        }

        Provider created = providerService.createProvider(
            body.get("name"),
            body.get("address"),
            body.get("location"),
            body.get("operatingHours"),
            body.get("services"),
            body.get("pricing"),
            userId
        );

        if (created == null)
            return ResponseEntity.status(409).body("A provider profile already exists for this user");

        return ResponseEntity.status(201).body(created);
    }

    /**
     * GET /services/{sid}
     * Get a full service profile including its vets and rating.
     * Access: Public
     */
    @GetMapping("/services/{sid}")
    public ResponseEntity<Object> getService(@PathVariable int sid) {
        Optional<Provider> found = providerService.getProviderById(sid);
        if (found.isEmpty())
            return ResponseEntity.status(404).body("Service not found");

        Provider provider = found.get();
        List<Veterinarian> vets = providerService.getVetsForProvider(sid);

        Map<String, Object> response = Map.of(
            "service", provider,
            "veterinarians", vets != null ? vets : List.of(),
            "avgRating", provider.getAvgRating()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /services/{sid}
     * Update a service profile.
     * Access: Authenticated (Provider)
     */
    @PutMapping("/services/{sid}")
    public ResponseEntity<Object> updateService(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int sid,
            @RequestBody Map<String, String> fields) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        Provider updated = providerService.updateProvider(sid, fields);
        if (updated == null)
            return ResponseEntity.status(404).body("Service not found");

        return ResponseEntity.ok(updated);
    }

    // -------------------------------------------------------------------------
    // Veterinarian endpoints  —  /services/{sid}/veterinarians
    // -------------------------------------------------------------------------

    /**
     * GET /services/{sid}/veterinarians
     * Access: Public
     */
    @GetMapping("/services/{sid}/veterinarians")
    public ResponseEntity<Object> listVets(@PathVariable int sid) {
        List<Veterinarian> vets = providerService.getVetsForProvider(sid);
        if (vets == null)
            return ResponseEntity.status(404).body("Service not found");
        return ResponseEntity.ok(vets);
    }

    /**
     * POST /services/{sid}/veterinarians
     * Add a vet — { name, specialization } for a new vet, or { vetId } to link an existing one.
     * Access: Authenticated (Provider)
     */
    @PostMapping("/services/{sid}/veterinarians")
    public ResponseEntity<Object> addVet(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int sid,
            @RequestBody Map<String, String> body) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        if (body.containsKey("vetId")) {
            int vetId;
            try {
                vetId = Integer.parseInt(body.get("vetId"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("vetId must be a valid integer");
            }
            ProviderVet link = providerService.linkExistingVetToProvider(sid, vetId);
            if (link == null)
                return ResponseEntity.status(409).body("Vet not found or already linked to this service");
            return ResponseEntity.status(201).body(link.getVeterinarian());
        }

        Veterinarian created = providerService.addNewVetToProvider(sid, body.get("name"), body.get("specialization"));
        if (created == null)
            return ResponseEntity.status(404).body("Service not found");
        return ResponseEntity.status(201).body(created);
    }

    /**
     * DELETE /services/{sid}/veterinarians/{vid}
     * Access: Authenticated (Provider)
     */
    @DeleteMapping("/services/{sid}/veterinarians/{vid}")
    public ResponseEntity<Object> removeVet(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int sid,
            @PathVariable int vid) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        boolean removed = providerService.removeVetFromProvider(sid, vid);
        if (!removed)
            return ResponseEntity.status(404).body("Vet or service not found");
        return ResponseEntity.ok("Veterinarian removed from service");
    }

    /**
     * GET /veterinarians/{vid}
     * Access: Public
     */
    @GetMapping("/veterinarians/{vid}")
    public ResponseEntity<Object> getVet(@PathVariable int vid) {
        Optional<Veterinarian> found = providerService.getVetById(vid);
        if (found.isEmpty())
            return ResponseEntity.status(404).body("Veterinarian not found");
        return ResponseEntity.ok(found.get());
    }

    // -------------------------------------------------------------------------
    // Booking endpoints  —  /services/{sid}/bookings  &  /bookings
    // -------------------------------------------------------------------------

    /**
     * POST /services/{sid}/bookings
     * Pet owner creates a booking with a provider.
     * Access: Authenticated (Pet Owner)
     * Body: { petOwnerId, date, time, serviceType, notes }
     */
    @PostMapping("/services/{sid}/bookings")
    public ResponseEntity<Object> createBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int sid,
            @RequestBody Map<String, String> body) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        int petOwnerId;
        try {
            petOwnerId = Integer.parseInt(body.get("petOwnerId"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("petOwnerId must be a valid integer");
        }

        int petId;
        try {
            petId = Integer.parseInt(body.get("petId"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("petId must be a valid integer");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(body.get("date"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD");
        }

        java.time.LocalTime time;
        try {
            time = java.time.LocalTime.parse(body.get("time"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid time format. Use HH:MM");
        }

        Object result = providerService.createBooking(
            sid, petOwnerId, petId, date, time,
            body.get("serviceType"),
            body.get("notes")
        );

        if (result == null)
            return ResponseEntity.status(404).body("Service not found");
        if ("duplicate".equals(result))
            return ResponseEntity.status(409).body("This pet already has a booking with this provider on that day");

        return ResponseEntity.status(201).body(result);
    }

    /**
     * GET /services/{sid}/bookings
     * Provider views all bookings for their service.
     * Access: Authenticated (Provider)
     * Optional params: date (YYYY-MM-DD), status (PENDING/CONFIRMED/CANCELLED)
     */
    @GetMapping("/services/{sid}/bookings")
    public ResponseEntity<Object> getBookingsForService(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int sid,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        LocalDate parsedDate = null;
        if (date != null) {
            try {
                parsedDate = LocalDate.parse(date);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD");
            }
        }

        List<Booking> bookings = providerService.getBookingsForProvider(sid, parsedDate, status);
        if (bookings == null)
            return ResponseEntity.status(404).body("Service not found");
        return ResponseEntity.ok(bookings);
    }

    /**
     * GET /bookings?petOwnerId={id}
     * Pet owner views all their own bookings.
     * Access: Authenticated (Pet Owner)
     */
    @GetMapping("/bookings")
    public ResponseEntity<Object> getMyBookings(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int petOwnerId) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        List<Booking> bookings = providerService.getBookingsForPetOwner(petOwnerId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * PATCH /services/{sid}/bookings/{bid}/confirm
     * Provider confirms a pending booking.
     * Access: Authenticated (Provider)
     */
    @PatchMapping("/services/{sid}/bookings/{bid}/confirm")
    public ResponseEntity<Object> confirmBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int sid,
            @PathVariable int bid) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        Booking updated = providerService.confirmBooking(bid);
        if (updated == null)
            return ResponseEntity.status(409).body("Booking not found or not in PENDING state");
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /services/{sid}/bookings/{bid}/cancel
     * Cancel a booking — provider or pet owner.
     * Access: Authenticated
     */
    @PatchMapping("/services/{sid}/bookings/{bid}/cancel")
    public ResponseEntity<Object> cancelBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int sid,
            @PathVariable int bid) {

        if (extractAndValidate(authHeader) == null)
            return ResponseEntity.status(401).body("Invalid or missing token");

        Booking updated = providerService.cancelBooking(bid);
        if (updated == null)
            return ResponseEntity.status(409).body("Booking not found or already cancelled");
        return ResponseEntity.ok(updated);
    }

    // -------------------------------------------------------------------------
    // Internal endpoints — service-to-service, no token required
    // -------------------------------------------------------------------------

    /**
     * PATCH /internal/services/{sid}/rating
     * Called by Review Service to push a new average rating for a provider.
     */
    @PatchMapping("/internal/services/{sid}/rating")
    public ResponseEntity<Object> updateServiceRating(
            @PathVariable int sid,
            @RequestBody Map<String, Double> body) {

        Double newRating = body.get("avgRating");
        if (newRating == null)
            return ResponseEntity.badRequest().body("avgRating is required");

        Provider updated = providerService.updateProviderRating(sid, newRating);
        if (updated == null)
            return ResponseEntity.status(404).body("Service not found");
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /internal/veterinarians/{vid}/rating
     * Called by Review Service to push a new average rating for a vet.
     */
    @PatchMapping("/internal/veterinarians/{vid}/rating")
    public ResponseEntity<Object> updateVetRating(
            @PathVariable int vid,
            @RequestBody Map<String, Double> body) {

        Double newRating = body.get("avgRating");
        if (newRating == null)
            return ResponseEntity.badRequest().body("avgRating is required");

        Veterinarian updated = providerService.updateVetRating(vid, newRating);
        if (updated == null)
            return ResponseEntity.status(404).body("Veterinarian not found");
        return ResponseEntity.ok(updated);
    }
}

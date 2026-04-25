package com.pawpal.provider.client;

/**
 * End-to-end demo of the Provider microservice via ProviderClient.
 *
 * Prerequisites:
 *   1. Auth service running on localhost:8080
 *   2. Provider service running on localhost:8082
 *
 * Run this after starting both services.
 */
public class ProviderClientDemo {

    public static void main(String[] args) {

        AuthClient authClient  = new AuthClient("http://localhost:8080");
        ProviderClient client  = new ProviderClient("http://localhost:8082");

        String timestamp = String.valueOf(System.currentTimeMillis());

        System.out.println("========================================");
        System.out.println("  PawPal Provider Service Demo");
        System.out.println("========================================\n");

        // --- Provider side ---

        // 1. Register a provider account
        System.out.println("1. Registering provider account...");
        String providerEmail = "provider_" + timestamp + "@pawpal.com";
        String providerRegister = authClient.register("Happy Paws Clinic", providerEmail, "pass123", "PROVIDER");
        System.out.println("   Response: " + providerRegister);

        // 2. Provider logs in
        System.out.println("\n2. Provider logging in...");
        String providerToken = authClient.login(providerEmail, "pass123");
        System.out.println("   Token: " + providerToken);

        int providerUserId = 1; // Replace with actual id parsed from register response

        // 3. Create a provider service profile
        System.out.println("\n3. Creating provider service profile...");
        String created = client.createService(
            providerToken,
            "Happy Paws Clinic",
            "123 Pet Street, Dubai Marina",
            "Dubai",
            "Mon-Sat 8am-8pm",
            "grooming,vet,daycare",
            "AED 150-600",
            providerUserId
        );
        System.out.println("   Created: " + created);

        int serviceId = 1; // Replace with actual id parsed from created response

        // 4. Get the service profile
        System.out.println("\n4. Getting service profile...");
        System.out.println("   Profile: " + client.getServiceById(serviceId));

        // 5. Search services by location
        System.out.println("\n5. Searching services in Dubai...");
        System.out.println("   Results: " + client.searchServices("grooming", "Dubai", null));

        // --- Pet Owner side ---

        // 6. Register a pet owner account
        System.out.println("\n6. Registering pet owner account...");
        String ownerEmail = "owner_" + timestamp + "@pawpal.com";
        String ownerRegister = authClient.register("Sara Ahmed", ownerEmail, "pass456", "PET_OWNER");
        System.out.println("   Response: " + ownerRegister);

        // 7. Pet owner logs in
        System.out.println("\n7. Pet owner logging in...");
        String ownerToken = authClient.login(ownerEmail, "pass456");
        System.out.println("   Token: " + ownerToken);

        int petOwnerId = 2; // Replace with actual id parsed from register response
        int petId = 1;      // Replace with actual pet id from a Pet service

        // 8. Pet owner creates a booking
        System.out.println("\n8. Pet owner creating a booking...");
        String booking = client.createBooking(
            ownerToken,
            serviceId,
            petOwnerId,
            petId,
            "2026-05-01",
            "10:00",
            "grooming",
            "Please use hypoallergenic shampoo"
        );
        System.out.println("   Booking: " + booking);

        int bookingId = 1; // Replace with actual id parsed from booking response

        // 9. Pet owner views their bookings
        System.out.println("\n9. Pet owner viewing their bookings...");
        System.out.println("   My bookings: " + client.getMyBookings(ownerToken, petOwnerId));

        // 10. Provider views bookings for their service
        System.out.println("\n10. Provider viewing bookings for their service...");
        System.out.println("    Bookings: " + client.getBookingsForService(providerToken, serviceId, null, null));

        // 11. Provider confirms the booking
        System.out.println("\n11. Provider confirming booking id=" + bookingId + "...");
        System.out.println("    Confirmed: " + client.confirmBooking(providerToken, serviceId, bookingId));

        // 12. Review Service pushes a new rating
        System.out.println("\n12. [Review Service] Pushing avgRating=4.7 to service...");
        System.out.println("    Updated: " + client.updateServiceRating(serviceId, 4.7));

        // 13. Pet owner cancels the booking
        System.out.println("\n13. Pet owner cancelling booking id=" + bookingId + "...");
        System.out.println("    Cancelled: " + client.cancelBooking(ownerToken, serviceId, bookingId));

        // 14. Logout both
        System.out.println("\n14. Logging out...");
        authClient.logout(providerToken);
        authClient.logout(ownerToken);
        System.out.println("    Logged out.");

        System.out.println("\n========================================");
        System.out.println("  All demo steps done!");
        System.out.println("========================================");
    }
}

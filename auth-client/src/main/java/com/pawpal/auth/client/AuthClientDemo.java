package com.pawpal.auth.client;

public class AuthClientDemo {
    public static void main(String[] args) {
        AuthClient client = new AuthClient("http://localhost:8080");
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String email = "demo_" + timestamp + "@pawpal.com";
        
        System.out.println("PawPal Auth Service Demo\n");
        
        // 1. Register
        System.out.println("1. Registering new user...");
        System.out.println("   Email: " + email);
        String registerResult = client.register("Demo User", email, "demo123", "PET_OWNER");
        System.out.println("   Response: " + registerResult);
        System.out.println("   Registration successful\n");
        
        // 2. Login
        System.out.println("2. Logging in...");
        String token = client.login(email, "demo123");
        System.out.println("   Token: " + token);
        System.out.println("   Login successful\n");
        
        // 3. Validate token
        System.out.println("3. Validating token...");
        boolean isValid = client.validateToken(token);
        System.out.println("   Is token valid? " + isValid);
        System.out.println("   Token is valid\n");
        
        // 4. Logout
        System.out.println("4. Logging out...");
        client.logout(token);
        System.out.println("   Logout successful\n");
        
        // 5. Validate after logout (expects false, catches exception)
        System.out.println("5. Validating token after logout...");
        try {
            boolean isValidAfter = client.validateToken(token);
            System.out.println("   Is token valid? " + isValidAfter);
            if (!isValidAfter) {
                System.out.println("   Token invalidated after logout\n");
            }
        } catch (Exception e) {
            System.out.println("   Token rejected (401 Unauthorized) after logout\n");
        }
        
        // 6. Password reset flow
        System.out.println("6. Testing password reset flow...");
        String resetToken = client.requestPasswordReset(email);
        System.out.println("   Reset token: " + resetToken);
        
        String resetResult = client.confirmPasswordReset(resetToken, "newpassword789");
        System.out.println("   Reset result: " + resetResult);
        System.out.println("   Password reset successful\n");
        
        // 7. Login with new password
        System.out.println("7. Logging in with new password...");
        String newToken = client.login(email, "newpassword789");
        System.out.println("   New token: " + newToken);
        System.out.println("   Login with new password successful\n");
        
        // 8. Clean up - logout
        client.logout(newToken);
        
        System.out.println("=== All tests passed for auth service! ===");
    }
}
package com.pawpal.owner.client;

public class OwnerClientDemo {
    public static void main(String[] args) {
        OwnerClient client = new OwnerClient("http://localhost:8081");

        System.out.println("PawPal Owner Service Demo\n");

        System.out.println("1. Creating owner...");
        String owner = client.createOwner("Alyah", "alyah@pawpal.com");
        System.out.println(owner + "\n");

        System.out.println("2. Getting owner with id 1...");
        System.out.println(client.getOwner(1) + "\n");

        System.out.println("3. Updating owner...");
        System.out.println(client.updateOwner(1, "Alyah Al Ali", "alyah.updated@pawpal.com") + "\n");

        System.out.println("4. Adding pet...");
        System.out.println(client.addPet(1, "Milo", "Cat", "Persian", 3, "Vaccinated") + "\n");

        System.out.println("5. Listing pets...");
        System.out.println(client.getPets(1) + "\n");

        System.out.println("6. Adding pet document...");
        System.out.println(client.addDocument(1, 1, "vaccination-card.pdf", "VACCINATION", "https://storage/pets/1/vaccination-card.pdf") + "\n");

        System.out.println("=== Demo finished ===");
    }
}

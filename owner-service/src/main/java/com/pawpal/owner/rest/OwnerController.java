package com.pawpal.owner.rest;

import com.pawpal.owner.model.Owner;
import com.pawpal.owner.model.Pet;
import com.pawpal.owner.model.PetDocument;
import com.pawpal.owner.service.AuthClient;
import com.pawpal.owner.service.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final AuthClient authClient;

    public OwnerController(OwnerService ownerService, AuthClient authClient) {
        this.ownerService = ownerService;
        this.authClient = authClient;
    }

    private String extractAndValidate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return authClient.validateToken(token) ? token : null;
    }

    @PostMapping
    public ResponseEntity<?> createOwner(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Owner owner
    ) {
        if (extractAndValidate(authHeader) == null) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }
        return new ResponseEntity<>(ownerService.createOwner(owner), HttpStatus.CREATED);
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<Owner> getOwner(@PathVariable int ownerId) {
        return ownerService.getOwner(ownerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<?> updateOwner(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int ownerId,
            @RequestBody Owner owner
    ) {
        if (extractAndValidate(authHeader) == null) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        return ownerService.updateOwner(ownerId, owner)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{ownerId}/pets")
    public ResponseEntity<List<Pet>> getPets(@PathVariable int ownerId) {
        if (ownerService.getOwner(ownerId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ownerService.getPets(ownerId));
    }

    @GetMapping("/{ownerId}/pets/{petId}")
    public ResponseEntity<Pet> getPet(@PathVariable int ownerId, @PathVariable int petId) {
        return ownerService.getPet(ownerId, petId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{ownerId}/pets")
    public ResponseEntity<?> createPet(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int ownerId,
            @RequestBody Pet pet
    ) {
        if (extractAndValidate(authHeader) == null) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        return ownerService.createPet(ownerId, pet)
                .map(savedPet -> new ResponseEntity<>(savedPet, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{ownerId}/pets/{petId}")
    public ResponseEntity<?> updatePet(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int ownerId,
            @PathVariable int petId,
            @RequestBody Pet pet
    ) {
        if (extractAndValidate(authHeader) == null) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        return ownerService.updatePet(ownerId, petId, pet)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{ownerId}/pets/{petId}")
    public ResponseEntity<?> deletePet(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int ownerId,
            @PathVariable int petId
    ) {
        if (extractAndValidate(authHeader) == null) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        boolean deleted = ownerService.deletePet(ownerId, petId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{ownerId}/pets/{petId}/documents")
    public ResponseEntity<List<PetDocument>> getPetDocuments(@PathVariable int ownerId, @PathVariable int petId) {
        if (ownerService.getPet(ownerId, petId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ownerService.getPetDocuments(ownerId, petId));
    }

    @PostMapping("/{ownerId}/pets/{petId}/documents")
    public ResponseEntity<?> createPetDocument(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int ownerId,
            @PathVariable int petId,
            @RequestBody PetDocument document
    ) {
        if (extractAndValidate(authHeader) == null) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        return ownerService.createPetDocument(ownerId, petId, document)
                .map(savedDocument -> new ResponseEntity<>(savedDocument, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{ownerId}/pets/{petId}/documents/{documentId}")
    public ResponseEntity<?> deletePetDocument(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int ownerId,
            @PathVariable int petId,
            @PathVariable int documentId
    ) {
        if (extractAndValidate(authHeader) == null) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        boolean deleted = ownerService.deletePetDocument(ownerId, petId, documentId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
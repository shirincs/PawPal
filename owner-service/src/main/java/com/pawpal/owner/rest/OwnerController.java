package com.pawpal.owner.rest;

import com.pawpal.owner.model.Owner;
import com.pawpal.owner.model.Pet;
import com.pawpal.owner.model.PetDocument;
import com.pawpal.owner.service.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping
    public ResponseEntity<Owner> createOwner(@RequestBody Owner owner) {
        return new ResponseEntity<>(ownerService.createOwner(owner), HttpStatus.CREATED);
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<Owner> getOwner(@PathVariable int ownerId) {
        return ownerService.getOwner(ownerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<Owner>> getAllOwners() {
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<Owner> updateOwner(@PathVariable int ownerId, @RequestBody Owner owner) {
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
    public ResponseEntity<Pet> createPet(@PathVariable int ownerId, @RequestBody Pet pet) {
        return ownerService.createPet(ownerId, pet)
                .map(savedPet -> new ResponseEntity<>(savedPet, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{ownerId}/pets/{petId}")
    public ResponseEntity<Pet> updatePet(@PathVariable int ownerId, @PathVariable int petId, @RequestBody Pet pet) {
        return ownerService.updatePet(ownerId, petId, pet)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{ownerId}/pets/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable int ownerId, @PathVariable int petId) {
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
    public ResponseEntity<PetDocument> createPetDocument(
            @PathVariable int ownerId,
            @PathVariable int petId,
            @RequestBody PetDocument document
    ) {
        return ownerService.createPetDocument(ownerId, petId, document)
                .map(savedDocument -> new ResponseEntity<>(savedDocument, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{ownerId}/pets/{petId}/documents/{documentId}")
    public ResponseEntity<Void> deletePetDocument(
            @PathVariable int ownerId,
            @PathVariable int petId,
            @PathVariable int documentId
    ) {
        boolean deleted = ownerService.deletePetDocument(ownerId, petId, documentId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

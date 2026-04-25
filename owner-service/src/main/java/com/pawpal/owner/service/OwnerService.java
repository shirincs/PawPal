package com.pawpal.owner.service;

import com.pawpal.owner.model.Owner;
import com.pawpal.owner.model.Pet;
import com.pawpal.owner.model.PetDocument;
import com.pawpal.owner.repository.OwnerRepository;
import com.pawpal.owner.repository.PetDocumentRepository;
import com.pawpal.owner.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    private final PetDocumentRepository petDocumentRepository;

    public OwnerService(
            OwnerRepository ownerRepository,
            PetRepository petRepository,
            PetDocumentRepository petDocumentRepository
    ) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
        this.petDocumentRepository = petDocumentRepository;
    }

    public Owner createOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Optional<Owner> getOwner(int ownerId) {
        return ownerRepository.findById(ownerId);
    }

    public Optional<Owner> getOwnerByUserId(int userId) {
        return ownerRepository.findByUserId(userId);
    }
    
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Optional<Owner> updateOwner(int ownerId, Owner updatedOwner) {
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        if (ownerOptional.isEmpty()) {
            return Optional.empty();
        }

        Owner owner = ownerOptional.get();
        owner.setName(updatedOwner.getName());
        owner.setEmail(updatedOwner.getEmail());
        return Optional.of(ownerRepository.save(owner));
    }

    public List<Pet> getPets(int ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    public Optional<Pet> getPet(int ownerId, int petId) {
        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isPresent() && petOptional.get().getOwner() != null && petOptional.get().getOwner().getId() == ownerId) {
            return petOptional;
        }
        return Optional.empty();
    }

    public Optional<Pet> createPet(int ownerId, Pet pet) {
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        if (ownerOptional.isEmpty()) {
            return Optional.empty();
        }

        pet.setOwner(ownerOptional.get());
        return Optional.of(petRepository.save(pet));
    }

    public Optional<Pet> updatePet(int ownerId, int petId, Pet updatedPet) {
        Optional<Pet> petOptional = getPet(ownerId, petId);
        if (petOptional.isEmpty()) {
            return Optional.empty();
        }

        Pet pet = petOptional.get();
        pet.setName(updatedPet.getName());
        pet.setType(updatedPet.getType());
        pet.setBreed(updatedPet.getBreed());
        pet.setAge(updatedPet.getAge());
        pet.setHealth(updatedPet.getHealth());

        return Optional.of(petRepository.save(pet));
    }

    public boolean deletePet(int ownerId, int petId) {
        Optional<Pet> petOptional = getPet(ownerId, petId);
        if (petOptional.isEmpty()) {
            return false;
        }

        petRepository.delete(petOptional.get());
        return true;
    }

    public List<PetDocument> getPetDocuments(int ownerId, int petId) {
        Optional<Pet> petOptional = getPet(ownerId, petId);
        if (petOptional.isEmpty()) {
            return List.of();
        }

        return petDocumentRepository.findByPetId(petId);
    }

    public Optional<PetDocument> createPetDocument(int ownerId, int petId, PetDocument document) {
        Optional<Pet> petOptional = getPet(ownerId, petId);
        if (petOptional.isEmpty()) {
            return Optional.empty();
        }

        document.setPet(petOptional.get());
        return Optional.of(petDocumentRepository.save(document));
    }

    public boolean deletePetDocument(int ownerId, int petId, int documentId) {
        Optional<Pet> petOptional = getPet(ownerId, petId);
        if (petOptional.isEmpty()) {
            return false;
        }

        Optional<PetDocument> documentOptional = petDocumentRepository.findById(documentId);
        if (documentOptional.isEmpty()) {
            return false;
        }

        PetDocument document = documentOptional.get();
        if (document.getPet() == null || document.getPet().getId() != petId) {
            return false;
        }

        petDocumentRepository.delete(document);
        return true;
    }
}

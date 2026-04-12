package com.pawpal.owner.repository;

import com.pawpal.owner.model.PetDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetDocumentRepository extends JpaRepository<PetDocument, Integer> {
    List<PetDocument> findByPetId(int petId);
}

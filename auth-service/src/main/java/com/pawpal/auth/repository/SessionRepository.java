package com.pawpal.auth.repository;

import com.pawpal.auth.model.Session;
import com.pawpal.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session> findByTokenAndIsActiveTrue(String token);
    Optional<Session> findByUserAndIsActiveTrue(User user);
}
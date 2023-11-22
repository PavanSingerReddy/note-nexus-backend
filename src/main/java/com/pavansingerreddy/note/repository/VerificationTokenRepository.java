package com.pavansingerreddy.note.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.entity.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {

    VerificationToken findByToken(String token);

    Optional<VerificationToken> findByUser(User user);
    
}

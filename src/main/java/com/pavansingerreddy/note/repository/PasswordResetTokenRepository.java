package com.pavansingerreddy.note.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pavansingerreddy.note.entity.PasswordResetToken;
import com.pavansingerreddy.note.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {

    Optional<PasswordResetToken> findByUser(User user);

    PasswordResetToken findByToken(String token);
    
}

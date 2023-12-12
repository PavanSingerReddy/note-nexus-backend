package com.pavansingerreddy.note.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pavansingerreddy.note.entity.PasswordResetToken;
import com.pavansingerreddy.note.entity.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByUser(User user);

    PasswordResetToken findByToken(String token);

}

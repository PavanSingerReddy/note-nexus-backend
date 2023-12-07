package com.pavansingerreddy.note.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pavansingerreddy.note.entity.PasswordResetToken;
import com.pavansingerreddy.note.entity.User;

// The @Repository annotation tells Spring that this interface is a Repository. Repositories in Spring are used for data access. They can fetch, save, update, and delete data.
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    // This method will find a PasswordResetToken for a given User. It returns an
    // Optional, which might or might not contain a PasswordResetToken. If a
    // PasswordResetToken for the given User exists, the Optional contains it. If no
    // such PasswordResetToken exists, the Optional is empty.
    Optional<PasswordResetToken> findByUser(User user);

    // This method will find a PasswordResetToken by its token String.
    // If a PasswordResetToken with the given token exists, it is returned.
    // If no such PasswordResetToken exists, this method will return null.
    PasswordResetToken findByToken(String token);

}

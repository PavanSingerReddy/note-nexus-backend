package com.pavansingerreddy.note.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pavansingerreddy.note.entity.Users;
import com.pavansingerreddy.note.entity.VerificationToken;

// The @Repository annotation tells Spring that this interface is a Repository.
// Repositories in Spring are used for data access. They can fetch, save, update, and delete data.
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    // This method will find a VerificationToken by its token String.
    // If a VerificationToken with the given token exists, it is returned.
    // If no such VerificationToken exists, this method will return null.
    VerificationToken findByToken(String token);

    // This method will find a VerificationToken for a given User.
    // It returns an Optional, which might or might not contain a VerificationToken.
    // If a VerificationToken for the given User exists, the Optional contains it.
    // If no such VerificationToken exists, the Optional is empty.
    Optional<VerificationToken> findByUser(Users user);

}

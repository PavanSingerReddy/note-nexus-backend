package com.pavansingerreddy.note.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pavansingerreddy.note.entity.Users;

// The @Repository annotation tells Spring that this interface is a Repository.
// Repositories in Spring are used for data access. They can fetch, save, update, and delete data.
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    // This method will find a User by their email. It returns an Optional, which
    // might or might not contain a User. If a User with the given email exists, the
    // Optional contains it. If no such User exists, the Optional is empty.
    Optional<Users> findByEmail(String userEmail);

    // The @Transactional annotation tells Spring that this method should be run
    // within a transaction. Transactions in databases ensure that operations run
    // reliably and without interference from other operations. If something goes
    // wrong during the transaction (like an error occurs), all changes made during
    // the transaction are rolled back. This method will delete a User by their
    // email. If a User with the given email exists, they are deleted from the
    // database. If no such User exists, nothing happens.
    @Transactional
    void deleteByEmail(String userEmail);

    // Here we are writing a native sql query.This query gets the latest signed up
    // user who's mailNoToUseForSendingEmail has the lowest newUserCanBeCreatedAtTime

    // 1. The subquery SELECT MAX(u2.new_user_can_be_created_at_time) FROM User u2
    // GROUP BY
    // u2.mail_no_to_use_for_sending_email gets the latest newUserCanBeCreatedAtTime
    // for each mailNoToUseForSendingEmail.

    // 2. The outer query SELECT u.* FROM User AS u WHERE
    // u.new_user_can_be_created_at_time IN (...)
    // gets the users with these latest newUserCanBeCreatedAtTime.

    // 3. The final query SELECT u1.* FROM (...) AS u1 ORDER BY
    // u1.new_user_can_be_created_at_time
    // ASC LIMIT 1 gets the oldest user among these latest users.
    @Query(value = """
            SELECT u1.* FROM (\
            SELECT u.* FROM users AS u \
            WHERE u.new_user_can_be_created_at_time IN (\
            SELECT MAX(u2.new_user_can_be_created_at_time) FROM users u2 GROUP BY u2.mail_no_to_use_for_sending_email\
            )\
            ) AS u1 \
            ORDER BY u1.new_user_can_be_created_at_time ASC \
            LIMIT 1\
            """, nativeQuery = true)
    Optional<Users> findTheUserWhoContainsTheAppropriateEmailToSendSignUpUrl();

    Optional<Users> findByUsername(String dummyUsername1);

}

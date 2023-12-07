package com.pavansingerreddy.note.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pavansingerreddy.note.entity.User;

// The @Repository annotation tells Spring that this interface is a Repository.
// Repositories in Spring are used for data access. They can fetch, save, update, and delete data.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // This method will find a User by their email. It returns an Optional, which
    // might or might not contain a User. If a User with the given email exists, the
    // Optional contains it. If no such User exists, the Optional is empty.
    Optional<User> findByEmail(String userEmail);

    // The @Transactional annotation tells Spring that this method should be run
    // within a transaction. Transactions in databases ensure that operations run
    // reliably and without interference from other operations. If something goes
    // wrong during the transaction (like an error occurs), all changes made during
    // the transaction are rolled back. This method will delete a User by their
    // email. If a User with the given email exists, they are deleted from the
    // database. If no such User exists, nothing happens.
    @Transactional
    void deleteByEmail(String userEmail);

}

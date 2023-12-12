package com.pavansingerreddy.note.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pavansingerreddy.note.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String userEmail);

    @Transactional
    void deleteByEmail(String userEmail);

    @Query(value = """
            SELECT u1.* FROM (\
            SELECT u.* FROM user AS u \
            WHERE u.new_user_can_be_created_at_time IN (\
            SELECT MAX(u2.new_user_can_be_created_at_time) FROM user u2 GROUP BY u2.mail_no_to_use_for_sending_email\
            )\
            ) AS u1 \
            ORDER BY u1.new_user_can_be_created_at_time ASC \
            LIMIT 1\
            """, nativeQuery = true)
    Optional<User> findTheUserWhoContainsTheAppropriateEmailToSendSignUpUrl();

    Optional<User> findByUsername(String dummyUsername1);

}

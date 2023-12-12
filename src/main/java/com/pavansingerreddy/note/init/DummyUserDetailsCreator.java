package com.pavansingerreddy.note.init;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.repository.UserRepository;

@Component

public class DummyUserDetailsCreator implements ApplicationRunner {

    private final UserRepository userRepository;

    public DummyUserDetailsCreator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${mail.config1.username}")
    private String fromEmail1;

    @Value("${mail.config2.username}")
    private String fromEmail2;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String dummyUsername1 = "dummyUser1";
        String dummyUsername2 = "dummyUser2";

        Optional<User> existingUser1 = userRepository.findByUsername(dummyUsername1);
        Optional<User> existingUser2 = userRepository.findByUsername(dummyUsername2);

        if (!existingUser1.isPresent()) {
            User user1 = new User();
            user1.setUsername(dummyUsername1);
            user1.setPassword(passwordEncoder.encode("password1"));
            user1.setEmail(fromEmail1);
            user1.setMailNoToUseForSendingEmail(1);
            user1.setNewUserCanBeCreatedAtTime(Date.from(Instant.now()));
            user1.setUserCreatedAtTime(Date.from(Instant.now()));

            userRepository.save(user1);
        }

        if (!existingUser2.isPresent()) {

            User user2 = new User();
            user2.setUsername(dummyUsername2);
            user2.setPassword(passwordEncoder.encode("password2"));
            user2.setEmail(fromEmail2);
            user2.setMailNoToUseForSendingEmail(2);
            user2.setNewUserCanBeCreatedAtTime(Date.from(Instant.now()));
            user2.setUserCreatedAtTime(Date.from(Instant.now()));

            userRepository.save(user2);
        }
    }

}

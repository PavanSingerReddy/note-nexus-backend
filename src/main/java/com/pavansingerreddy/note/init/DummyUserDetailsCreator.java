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

import com.pavansingerreddy.note.entity.Users;
import com.pavansingerreddy.note.repository.UserRepository;

// Component annotation indicates that the class is a Spring component. Spring will automatically detect this class for dependency injection when component scanning is enabled.
@Component
// This declares a class DummyUserDetailsCreator that implements the
// ApplicationRunner
// interface. ApplicationRunner is a Spring Boot interface used to execute the
// code after the Spring Boot application is started.This is used to create a
// two new dummy users with the 2 email address and also it sets the 2 new
// mailNoToUseForSendingEmail variable for 2 new users which will be used by
// findTheUserWhoContainsTheAppropriateEmailToSendSignUpUrl() method of the user
// repository for informing then next new user who is going to created to use
// which mail number so that he can avoid getting banned by the mail providers
// like gmail and outlook for spamming
public class DummyUserDetailsCreator implements ApplicationRunner {

    // making the UserRepository as final so it can be assigned a value only once so
    // any other accidental assigning of other objects does not happen to it.
    private final UserRepository userRepository;

    // making the constructor for DummyUserDetailsCreator class and here the spring
    // injects
    // our UserRepository object or bean in the constructor by using dependency
    // injection in our parameter automatically so that we can use that object later
    // . Instead of all these we can also use autowired annotation
    public DummyUserDetailsCreator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${mail.config1.username}")
    private String fromEmail1;

    @Value("${mail.config2.username}")
    private String fromEmail2;

    @Autowired
    PasswordEncoder passwordEncoder;

    // This method is an override from the ApplicationRunner interface. It is called
    // just before SpringApplication.run(…) completes.
    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Define the usernames of the dummy users
        String dummyUsername1 = "dummyUser1";
        String dummyUsername2 = "dummyUser2";

        // Check if the dummy users already exist
        Optional<Users> existingUser1 = userRepository.findByUsername(dummyUsername1);
        Optional<Users> existingUser2 = userRepository.findByUsername(dummyUsername2);

        // This creates a User object, user1, and sets its username, password,
        // email, mailNoToUseForSendingEmail, newUserCanBeCreatedAtTime,
        // userCreatedAtTime

        // If dummy user1 doesn't exist, create it
        if (!existingUser1.isPresent()) {
            Users user1 = new Users();
            user1.setUsername(dummyUsername1);
            user1.setPassword(passwordEncoder.encode("password1"));
            user1.setEmail(fromEmail1);
            user1.setMailNoToUseForSendingEmail(1);
            user1.setNewUserCanBeCreatedAtTime(Date.from(Instant.now()));
            user1.setUserCreatedAtTime(Date.from(Instant.now()));
            // Set other properties for user1...

            // Save the dummy users to the database
            userRepository.save(user1);
        }

        // If dummy user2 doesn't exist, create it
        if (!existingUser2.isPresent()) {
            // This creates a User object, user2, and sets its username, password,
            // email, mailNoToUseForSendingEmail, newUserCanBeCreatedAtTime,
            // userCreatedAtTime
            Users user2 = new Users();
            user2.setUsername(dummyUsername2);
            user2.setPassword(passwordEncoder.encode("password2"));
            user2.setEmail(fromEmail2);
            user2.setMailNoToUseForSendingEmail(2);
            user2.setNewUserCanBeCreatedAtTime(Date.from(Instant.now()));
            user2.setUserCreatedAtTime(Date.from(Instant.now()));
            // Set other properties for user2...

            // Save the dummy users to the database
            userRepository.save(user2);
        }
    }

}

// Note : when you add a 3rd new additional mail service provider in the
// application.yml file then update this file and add another user object with
// the new mail details and also update MailConfig file and EmailService files
// to reflect the new mail changes
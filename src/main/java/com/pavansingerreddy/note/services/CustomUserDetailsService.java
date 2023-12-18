package com.pavansingerreddy.note.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pavansingerreddy.note.entity.Users;
import com.pavansingerreddy.note.repository.UserRepository;

// Service annotation is used in this code to indicate that Spring should automatically detect this as a service class.
@Service
// This class implements the UserDetailsService interface, which is used by
// Spring Security to handle user information.This class is used by the
// authentication provider to get the user based on the email and to verify the
// form details with the details present in our database
public class CustomUserDetailsService implements UserDetailsService {

    // Autowired annotation is used by Spring to automatically inject an object
    // dependency.
    @Autowired
    // This field is a PasswordEncoder, which is used to encode passwords.
    PasswordEncoder passwordEncoder;

    // Autowired annotation is used by Spring to automatically inject an object
    // dependency.
    @Autowired
    // This field is a UserRepository, which is used to interact with the User table
    // in the database.
    UserRepository userRepository;

    // When Spring encounters the @Transactional annotation, it automatically
    // creates a transaction around the annotated code and manages the transaction
    // lifecycle. This includes tasks like starting the transaction before the
    // method runs and committing the transaction after the method completes
    // successfully. If the method throws an exception, Spring will roll back the
    // transaction
    @Transactional
    // overriding the loadUserByUsername method of the UserDetailsService super
    // class
    @Override
    // This method is used to load a user by their username (in this case, email).
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // This line calls the findByEmail method of the UserRepository to find a user
        // by their email. The result is wrapped in an Optional.
        Optional<Users> userDetails = userRepository.findByEmail(email);
        // This checks if the Optional contains a User.
        if (userDetails.isPresent()) {
            // If the Optional contains a User, it returns the User.
            return userDetails.get();
        }
        // If the Optional does not contain a User, it throws a BadCredentialsException.
        throw new BadCredentialsException("Invalid credentials");
    }

}

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

import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.repository.UserRepository;

@Service

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired

    PasswordEncoder passwordEncoder;

    @Autowired

    UserRepository userRepository;

    @Transactional

    @Override

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> userDetails = userRepository.findByEmail(email);

        if (userDetails.isPresent()) {

            return userDetails.get();
        }

        throw new BadCredentialsException("Invalid credentials");
    }

}

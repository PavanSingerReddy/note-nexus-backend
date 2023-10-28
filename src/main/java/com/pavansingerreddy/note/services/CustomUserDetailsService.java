package com.pavansingerreddy.note.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @jakarta.transaction.Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
       Optional<User> userdetails =  userRepository.findByEmail(email);
        if(userdetails.isPresent()){
            return userdetails.get();
        }
        throw new BadCredentialsException("Invalid credentials");
    }
    
}

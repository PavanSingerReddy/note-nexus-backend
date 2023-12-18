package com.pavansingerreddy.note.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// declaring it as a component so spring can create beans of this custom filter
@Configuration
// PasswordEncoderConfig is a class which contains the configuration of our
// custom password encoder
public class PasswordEncoderConfig {
    // Bean annotation tells Spring that the annotated method will return an object
    // that should be registered as a bean in the Spring application context.
    @Bean
    // method returns a password encoder of our choice here in this case we are
    // returning BCryptPasswordEncoder as it also implements the PasswordEncoder
    PasswordEncoder getBCryptPasswordEncoder() {
        // This line creates a new BCryptPasswordEncoder object. BCryptPasswordEncoder
        // is a password encoder that uses the BCrypt strong hashing function
        return new BCryptPasswordEncoder();
    }
}

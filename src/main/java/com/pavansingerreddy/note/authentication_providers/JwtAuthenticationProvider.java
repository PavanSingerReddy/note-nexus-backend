package com.pavansingerreddy.note.authentication_providers;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pavansingerreddy.note.entity.User;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("=======================AUTHENTICATION HAPPENING WITH CUSTOM JWT AUTHENTICATION===========");

        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        // fetching user details by his email id
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // creating a new user object so that we can copy user details from userDetails to user as userDetails is not accepting the userDetails.getEmail() method in the below usernamePasswordAuthenticationToken
        User user = new User();
        // copying properties from userDetails to user
        BeanUtils.copyProperties(userDetails, user);

        // checking if the user is enabled or not if the user is not enabled then we are sending an exception with "user is disabled" message
        if (!userDetails.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        if (userDetails != null) {
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        user.getEmail(), userDetails.getPassword(), userDetails.getAuthorities());
                // here we have used
                // UsernamePasswordAuthenticationToken(user.getEmail(),password,userDetails.getAuthorities());
                // instead of
                // UsernamePasswordAuthenticationToken(username,password,userDetails.getAuthorities());
                // because we are using email as an identifier for the user this emailid
                // reflects in the "sub" claim of the jwt which we are going to use to
                // authenticate the user
                return usernamePasswordAuthenticationToken;
            }
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

}

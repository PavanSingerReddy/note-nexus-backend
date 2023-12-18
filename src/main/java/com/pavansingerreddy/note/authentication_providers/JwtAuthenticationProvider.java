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

import com.pavansingerreddy.note.entity.Users;

// declaring it as a component so spring can create beans of this custom filter
@Component
// defining our custom authentication provider which authenticates the user
// based on the details of his username and password which was provided to us by
// the Authentication object if the user is not present in our database then we
// throw an exception which rejects the authentication of the user so user will
// not be authenticated.If the user is present then we call the
// UsernamePasswordAuthenticationToken with three parameters which authenticates
// the user.Our custom JwtAuthenticationProvider implements the
// AuthenticationProvider which is a interface containing the abstract method
// named "authenticate"
public class JwtAuthenticationProvider implements AuthenticationProvider {

    // Autowiring a PasswordEncoder class which gives us an object(bean) of
    // PasswordEncoder from the spring IOC container.It uses the
    // BcryptPasswordEncoder as we have configured it in another class
    @Autowired
    private PasswordEncoder passwordEncoder;

    // getting our custom UserDetailsService implementation by autowiring the
    // UserDetailsService
    @Autowired
    private UserDetailsService userDetailsService;

    // overriding our authenticate object from the authentication provider
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // getting the email from the authentication object
        String email = authentication.getName();
        // getting the password from the authentication object
        String password = String.valueOf(authentication.getCredentials());

        // fetching user details by his email id
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // creating a new user object so that we can copy user details from userDetails
        // to user as userDetails is not accepting the userDetails.getEmail() method in
        // the below usernamePasswordAuthenticationToken
        Users user = new Users();
        // copying properties from userDetails to user
        BeanUtils.copyProperties(userDetails, user);

        // checking if the user is enabled or not if the user is not enabled then we are
        // sending an exception with "user is disabled" message
        if (!userDetails.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        // if the user is not null and the password of the user from our request matches
        // from the password in the database then we authenticate the user
        if (userDetails != null) {
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                // here we have used
                // UsernamePasswordAuthenticationToken(user.getEmail(),password,userDetails.getAuthorities());
                // instead of
                // UsernamePasswordAuthenticationToken(username,password,userDetails.getAuthorities());
                // because we are using email as an identifier for the user this emailid
                // reflects in the "sub" claim of the jwt which we are going to use to
                // authenticate the user
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        user.getEmail(), userDetails.getPassword(), userDetails.getAuthorities());
                return usernamePasswordAuthenticationToken;
            }
        }

        // if any of the above conditions does not satisfy then we are throwing
        // BadCredentialsException
        throw new BadCredentialsException("Invalid credentials");
    }

    // Authentication Manager checks if the token is supported by this filter
    // to avoid unnecessary checks.
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

}

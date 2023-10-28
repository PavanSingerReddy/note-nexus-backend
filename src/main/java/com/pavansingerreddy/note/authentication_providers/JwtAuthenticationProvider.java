package com.pavansingerreddy.note.authentication_providers;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
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

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        User user = new User();
        BeanUtils.copyProperties(userDetails, user);

        if(userDetails!=null){
            if(passwordEncoder.matches(password, userDetails.getPassword())){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(),password,userDetails.getAuthorities());
                // here we have used UsernamePasswordAuthenticationToken(user.getEmail(),password,userDetails.getAuthorities()); instead of UsernamePasswordAuthenticationToken(username,password,userDetails.getAuthorities()); because we are using email as an identifier for the user this emailid reflects in the "sub" claim of the jwt which we are going to use to authenticate the user
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

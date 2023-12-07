package com.pavansingerreddy.note.authentication_filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pavansingerreddy.note.utils.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

// declaring it as a component so spring can create beans of this custom filter
@Component
// This is JWTTokenFilter which is a custom implementation of my authentication
// filter which checks if the jwt is present or not and if the jwt token is
// valid or not based on that it authenticates the request
public class JWTTokenFilter extends OncePerRequestFilter {

    // Autowiring a JWTUtil class which gives us an object(bean) of JWTUtil from the
    // spring IOC container. JWTUtil is a utility class which is used for managing
    // the json web tokens(jwt's)
    @Autowired
    private JWTUtil jwtUtil;

    // as we are implementing OncePerRequestFilter it contains the abstract
    // doFilterInternal method here we are providing the implementation for that
    // method
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // here we are trying to find the JWT token from the cookie sent by our client
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("JWT")) {
                    token = cookie.getValue();
                }
            }
        }

        // if the jwt token is null or jwt token is not valid then we are moving on to
        // the next filter.As we are not authenticating the user in this filter in the
        // subsequent filters also user will not be authenticated as we did not added
        // any custom authentication filter in the next filters
        if (token == null || !jwtUtil.validateJwt(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // extracting the username or email from the jwt token of the user's request
        // which contains the jwt cookie
        String username = jwtUtil.getUsername(token);

        // the getRoles method from the jwtUtil class takes the jwt token and gives the
        // List of SimpleGrantedAuthority object which contains the roles associated
        // with that jwt token
        List<SimpleGrantedAuthority> roles = jwtUtil.getRoles(token);

        // here we are creating the instance of UsernamePasswordAuthenticationToken and
        // we are calling the UsernamePasswordAuthenticationToken constructor with three
        // parameters which authenticates the user's current request
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                username, null, roles);

        // the setDetails is used to store additional details about the authenticating request These details can be accessed by our application for various purposes.The buildDetails(HttpServletRequest) method creates an instance of WebAuthenticationDetails containing details of the web-based authentication request, such as the IP address.
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // setting the security context holder to our authenticated usernamePasswordAuthenticationToken so that we can access the user's information like his email id etc in our application api's by defining Authentication object in our method parameters
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        // after authenticating the user we are delegating the request and response objects to the next filters
        filterChain.doFilter(request, response);

    }

}

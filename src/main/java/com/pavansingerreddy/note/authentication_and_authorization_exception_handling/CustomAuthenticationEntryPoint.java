package com.pavansingerreddy.note.authentication_and_authorization_exception_handling;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// declaring it as a component so spring can create beans of this custom filter
@Component
// implementing our custom AuthenticationEntryPoint which get's triggered if the
// unauthenticated user who has not logged in and tries to access a resource
// which requires the user to authenticate.To implement our custom
// AuthenticationEntryPoint we need to implement the AuthenticationEntryPoint
// interface which contains a abstract method called "commence"
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // overriding the commence method which handles any authentication errors
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // we are adding a header named "access_denied_reason" which contains the reason
        // for denying the access to a particular resource
        response.addHeader("access_denied_reason", "User is not authenticated");
        // we are also adding the 401 Http response which indicates that Although the
        // HTTP standard specifies "unauthorized", semantically this response means
        // "unauthenticated". That is, the client must authenticate itself to get the
        // requested response.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // giving the response to the user using the PrintWriter object
        PrintWriter out = response.getWriter();
        out.println("request is unauthenticated. Please authenticate first");
    }

}

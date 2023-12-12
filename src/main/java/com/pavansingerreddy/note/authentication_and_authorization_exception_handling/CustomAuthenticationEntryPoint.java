package com.pavansingerreddy.note.authentication_and_authorization_exception_handling;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.addHeader("access_denied_reason", "User is not authenticated");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        PrintWriter out = response.getWriter();
        out.println("request is unauthenticated. Please authenticate first");
    }

}

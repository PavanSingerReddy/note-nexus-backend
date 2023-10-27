package com.pavansingerreddy.note.authentication_and_authorization_exception_handling;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
                
                response.addHeader("access_denied_reason", "Access Denied. User does not have permissions to access this resource");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = response.getWriter();
                out.println("request is unauthorized !!! you donot have sufficient permissions");
        }
    
}

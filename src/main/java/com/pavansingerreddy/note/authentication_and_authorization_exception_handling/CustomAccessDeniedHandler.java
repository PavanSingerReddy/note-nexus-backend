package com.pavansingerreddy.note.authentication_and_authorization_exception_handling;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// declaring it as a component so spring can create beans of this custom filter
@Component
// implementing our custom AccessDeniedHandler which get's triggered if the
// unauthorized user who has no permissions to access a resource accesses the
// resource.To implement our custom AccessDeniedHandler we need to implement the
// AccessDeniedHandler interface which contains a abstract method called
// "handle"
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

        // overriding the handle method which handles any authorization errors
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException, ServletException {

                // we are adding a header named "access_denied_reason" which contains the reason
                // for denying the access to a particular resource
                response.addHeader("access_denied_reason",
                                "Access Denied. User does not have permissions to access this resource");
                // we are also adding the 403 Http response which indicates that the client does not have access rights to a particular resource; that is, it is unauthorized, so the server is refusing to give the requested resource. Unlike 401 Unauthorized, the client's identity is known to the server.
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                // giving the response to the user using the PrintWriter object
                PrintWriter out = response.getWriter();
                out.println("request is unauthorized !!! you do not have sufficient permissions");
        }

}

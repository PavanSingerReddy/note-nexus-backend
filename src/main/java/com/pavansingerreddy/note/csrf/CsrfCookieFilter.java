package com.pavansingerreddy.note.csrf;

import java.io.IOException;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// This line is declaring a new class named CsrfCookieFilter that extends (inherits from) the OncePerRequestFilter class. The final keyword means that this class cannot be extended by any other class
final public class CsrfCookieFilter extends OncePerRequestFilter {

	@Override
	// This line is declaring a method named doFilterInternal that overrides the
	// doFilterInternal method in the superclass OncePerRequestFilter. This method
	// takes three parameters: a HttpServletRequest, a HttpServletResponse, and a
	// FilterChain. It throws ServletException and IOException
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// This block of code checks if the request contains an attribute named _csrf
		// and that its value is not an empty string.
		if (request.getAttribute("_csrf") != null && !request.getAttribute("_csrf").toString().equals("")) {
			// If the above condition is true, this line retrieves the _csrf attribute from
			// the request and casts it to a CsrfToken object
			CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
			// This line calls the getToken method on the csrfToken object. This causes the
			// deferred token to be loaded, effectively rendering the token value to a
			// cookie.

			// Render the token value to a cookie by causing the deferred token to be loaded
			csrfToken.getToken();

		}

		// This line is executed after the above code. It allows the request to proceed
		// to the next filter in the chain.
		filterChain.doFilter(request, response);
	}

}

// In summary, this class is a filter that checks for a CSRF token in the
// request attributes. If it finds one, it loads the token (which has the side
// effect of rendering the token value to a cookie), and then allows the request
// to proceed to the next filter in the chain.

// Note: The csrfToken.getToken(); line is a method call that retrieves the CSRF
// token value from the csrfToken object.

// In the context of my code, this CSRF token is a unique identifier that’s
// associated with a user’s session and is used to prevent Cross-Site Request
// Forgery (CSRF) attacks.

// Now, we might wonder why we’re calling getToken() and not doing anything with
// the returned value. The key here is in the comment above the method call:
// “Render the token value to a cookie by causing the deferred token to be
// loaded”.

// This suggests that the getToken() method has a side effect. A side effect is
// an action that occurs as a result of calling a method, other than returning a
// value. In this case, the side effect is that the CSRF token value is stored
// (“rendered”) in a cookie.

// So, even though it looks like the getToken() method is being called and its
// return value is being ignored, what’s actually happening is that the CSRF
// token value is being stored in a cookie as a result of calling this method.

// This can be useful in scenarios where the token needs to be accessed by
// client-side scripts or included in subsequent requests from the client to the
// server.
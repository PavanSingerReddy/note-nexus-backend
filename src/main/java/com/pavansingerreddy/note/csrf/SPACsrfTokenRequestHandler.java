package com.pavansingerreddy.note.csrf;

import java.util.function.Supplier;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// SPACsrfTokenRequestHandler is a csrf token request handler for single page applications like react and angular.we are extending CsrfTokenRequestAttributeHandler for opting out of BREACH protection as we are using single page applications
final public class SPACsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
	// The below line is declaring a new class named SPACsrfTokenRequestHandler that
	// extends (inherits from) the CsrfTokenRequestAttributeHandler class. The final
	// keyword means that this class cannot be extended by any other
	// class.XorCsrfTokenRequestAttributeHandler is for request that require breach
	// protection
	private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

	@Override
	// This line is declaring a method named handle that overrides the handle method
	// in the superclass CsrfTokenRequestAttributeHandler. This method takes three
	// parameters: a HttpServletRequest, a HttpServletResponse, and a Supplier of
	// CsrfToken
	public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
		/*
		 * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection
		 * of
		 * the CsrfToken when it is rendered in the response body.
		 */

		// This line is calling the handle method on the delegate object, passing in the
		// request, response, and csrfToken parameters. This provides BREACH protection
		// of the CsrfToken when it is rendered in the response body.
		this.delegate.handle(request, response, csrfToken);
	}

	@Override
	// This line is declaring a method named resolveCsrfTokenValue that overrides
	// the resolveCsrfTokenValue method in the superclass
	// CsrfTokenRequestAttributeHandler. This method takes two parameters: a
	// HttpServletRequest and a CsrfToken
	public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
		/*
		 * If the request contains a request header, use
		 * CsrfTokenRequestAttributeHandler
		 * to resolve the CsrfToken. This applies when a single-page application
		 * includes
		 * the header value automatically, which was obtained via a cookie containing
		 * the
		 * raw CsrfToken.
		 */

		// This block of code checks if the request contains a request header with the
		// name of the CSRF token. If it does, it calls the resolveCsrfTokenValue method
		// from the superclass to resolve the CSRF token value.
		if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
			return super.resolveCsrfTokenValue(request, csrfToken);
		}
		/*
		 * In all other cases (e.g. if the request contains a request parameter), use
		 * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
		 * when a server-side rendered form includes the _csrf request parameter as a
		 * hidden input.
		 */
		return this.delegate.resolveCsrfTokenValue(request, csrfToken);
	}
}

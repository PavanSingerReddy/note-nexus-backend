package com.pavansingerreddy.note.csrf;

import java.io.IOException;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final public class CsrfCookieFilter extends OncePerRequestFilter {

	@Override

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (request.getAttribute("_csrf") != null && !request.getAttribute("_csrf").toString().equals("")) {

			CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

			csrfToken.getToken();

		}

		filterChain.doFilter(request, response);
	}

}

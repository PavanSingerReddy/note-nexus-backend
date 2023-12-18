package com.pavansingerreddy.note.config;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pavansingerreddy.note.authentication_filter.JWTTokenFilter;
import com.pavansingerreddy.note.authentication_providers.JwtAuthenticationProvider;
import com.pavansingerreddy.note.csrf.CsrfCookieFilter;
import com.pavansingerreddy.note.csrf.SPACsrfTokenRequestHandler;

// configuration annotation is used on classes which define beans. @Configuration classes are also @Component classes, so they are candidates for component scanning. But they have an added benefit: they can also define @Bean methods, which return instances of beans. These beans are managed by Spring and can be injected into other beans.
@Configuration

// The @EnableWebSecurity annotation in Spring Security is used to enable web
// security support in the application. This annotation is crucial because it
// does a couple of important things:

// 1. It imports the SpringSecurityFilterChain, which is a security filter chain
// that is automatically applied to all incoming requests.

// 2. It enables the @Configuration annotation on the class. This means that the
// annotated class can be used by Spring IoC container as a source of bean
// definitions.

// Without the @EnableWebSecurity annotation, the Spring Security configuration
// would not be fully set up, and your application would not be secured.
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, // enables the JSR-250 standard java security annotations, like
                                            // @RolesAllowed
        prePostEnabled = true // enables the PreAuthorize and PostAuthorize annotations
)
public class SecurityConfig {

    // Autowiring a JWTTokenFilter class which gives us an object(bean) of
    // JWTTokenFilter from the spring IOC container.JWTTokenFilter is a custom
    // filter which is used to check if the user is authenticated or not
    @Autowired
    private JWTTokenFilter jwtTokenFilter;

    // Autowiring a JwtAuthenticationProvider class which gives us an object(bean)
    // of JwtAuthenticationProvider from the spring IOC container.it is one of the
    // authentication provider in the authentication manager
    @Autowired
    JwtAuthenticationProvider jwtAuthenticationProvider;

    // Autowiring a AuthenticationEntryPoint class which gives us an object(bean) of
    // AuthenticationEntryPoint from the spring IOC
    // container.customAuthenticationEntryPoint is our custom implementation of the
    // AuthenticationEntryPoint which get's triggered when ever there is any
    // authentication errors
    @Autowired
    AuthenticationEntryPoint customAuthenticationEntryPoint;

    // Autowiring a AccessDeniedHandler class which gives us an object(bean) of
    // AccessDeniedHandler from the spring IOC container.customAccessDeniedHandler
    // is our custom implementation of the AccessDeniedHandler which get's triggered
    // when ever there is any authorization errors or whenever we are trying to
    // access a resource which we don't have permission
    @Autowired
    AccessDeniedHandler customAccessDeniedHandler;

    // Bean annotation tells Spring that the authenticationManager() method will
    // return an object that should be registered as a bean in the Spring
    // application context.
    @Bean
    // defining our AuthenticationManager bean which uses
    // new ProviderManager (which is a concrete implementation of
    // AuthenticationManager) with jwtAuthenticationProvider as its only
    // AuthenticationProvider
    AuthenticationManager authenticationManager() {
        return new ProviderManager(jwtAuthenticationProvider);
    }

    // Bean annotation tells Spring that the method will return an object that
    // should be registered as a bean in the Spring application context.
    @Bean
    SecurityFilterChain loginAndRegisterSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // CookieCsrfTokenRepository is a type of CsrfTokenRepository which is used to
        // manage the csrf token like getting the csrf cookie from the incoming http
        // request and generating a new csrf Token(actually DeferredCsrfToken calls the
        // "this.csrfTokenRepository.generateToken(this.request)" in it's init method if
        // csrf token cookie is not present) if the csrf token cookie is not present in
        // the incoming request etc
        CookieCsrfTokenRepository tokenRepository = new CookieCsrfTokenRepository();
        // using this setCookieCustomizer method we can set the properties of our csrf
        // cookie which is sent to our front end from the backend.Consumer is a
        // functional interface which has the "accept" method we have to override that
        // method with our csrf token cookie properties.the Consumer functional
        // interface here takes the ResponseCookieBuilder as an Argument
        tokenRepository.setCookieCustomizer(new Consumer<ResponseCookieBuilder>() {

            // here we override the accept method and specify the properties of the csrf
            // token cookie which is going to be created and sent to our frontend client
            // like react or angular
            @Override
            public void accept(ResponseCookieBuilder t) {
                // un-comment below line if you want the csrf token cookie to have the same site
                // attribute as none.But if it has same site as none then it should be secure
                // also if it is not secure and same site is none then chrome browser will
                // refuse to set the cookie

                // t.sameSite("none");

                // set the csrf token cookie as a secure cookie which is useful if we want our
                // cookie to be accessible if the site is on https only.here it is set to false
                // means this cookie is accessible on non secure sites like http also
                t.secure(false);
                // http only property of the csrf ensures that the cookie is not accessible by
                // the javascript running in the browser but will be sent with each request as a
                // cookie.here we are setting it to false because we want our frontend
                // application to access this cookie and set the "X-Xsrf-Token" http header with
                // our csrf token value
                t.httpOnly(false);
            }

        });
        // httpSecurity is the HttpSecurity instance that we're configuring based on our
        // needs.
        return httpSecurity
                // Configuring Cross-Site Request Forgery (CSRF) protection.
                .csrf((csrf) -> csrf
                        // Using a CSRF token repository(tokenRepository is a object of
                        // CookieCsrfTokenRepository) that stores the CSRF token in a cookie.
                        .csrfTokenRepository(tokenRepository)
                        // Using a custom CSRF token request handler which resolves the csrf token
                        // provided by the client.The csrf token will be generally included in the
                        // request header from the client when we use single page applications like
                        // react or angular.
                        .csrfTokenRequestHandler(new SPACsrfTokenRequestHandler()))
                // The CsrfCookieFilter is added after the BasicAuthenticationFilter because you
                // want to perform CSRF validation after the user has been authenticated. This
                // is because CSRF protection is typically needed for requests that could change
                // the state on the server side (like a money transfer in a banking
                // application), and these requests should be authenticated.
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                // Configuring authorization for HTTP requests.
                .authorizeHttpRequests(auth -> {
                    // Permitting all requests to "/api/user/login" without authenticating and all
                    // the other following endpoints like /api/user/register,/api/user/csrf-token ,
                    // etc.
                    auth.requestMatchers("/api/user/login").permitAll();
                    auth.requestMatchers("/api/user/register").permitAll();
                    auth.requestMatchers("/api/user/csrf-token").permitAll();
                    auth.requestMatchers("/api/user/verifyRegistration").permitAll();
                    auth.requestMatchers("/api/user/resendVerifyToken").permitAll();
                    auth.requestMatchers("/api/user/resetPassword").permitAll();
                    auth.requestMatchers("/api/user/verifyResetPassword").permitAll();
                    auth.requestMatchers("/api/user/isValidPasswordResetToken").permitAll();
                    // Requiring authentication for all other requests.
                    auth.anyRequest().authenticated();
                })
                // Configuring the application to be stateless, i.e., not to create a session as
                // we are using jwt's.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Adding our custom jwtTokenFilter filter before the
                // UsernamePasswordAuthenticationFilter.our jwtTokenFilter checks if the user is
                // authenticated or not
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // Configuring exception handling.
                .exceptionHandling((exceptionHandling) -> {
                    // Using a custom authentication entry point for handling authentication related
                    // exceptions.
                    exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint);
                    // Using a custom access denied handler for handling authorization related
                    // exceptions.
                    exceptionHandling.accessDeniedHandler(customAccessDeniedHandler);
                })
                // Configuring logout handling.
                .logout((logout) -> logout
                        // Setting the logout URL.
                        .logoutUrl("/api/user/logout")
                        // Using a logout success handler that returns the HTTP status like 200(ok) if
                        // the logout is successful.
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                        // Deleting the "JWT" cookie upon logout and the XSRF-TOKEN cookie is
                        // automatically delete by default when we logout if we want any other cookies
                        // to be deleted we can specify here.
                        .deleteCookies("JWT"))
                // Configuring headers.
                .headers((headers) -> {
                    // Enabling XSS protection with default settings for the older browsers and has
                    // some security flaws.
                    headers.xssProtection(Customizer.withDefaults());
                    // Setting the Content Security Policy to only allow scripts from the same
                    // origin.this CSP is for enabling Xss protection for newer browsers.
                    headers.contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'"));
                })
                // Configuring Cross-Origin Resource Sharing (CORS) with a custom configuration
                // source.
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
                // Building the SecurityFilterChain.
                .build();
    }

    // Bean annotation tells Spring that this method will return a bean that should
    // be managed by the Spring container.
    @Bean
    // This method returns a CorsConfigurationSource object.
    CorsConfigurationSource corsConfigurationSource() {
        // Create a new CorsConfiguration object.
        CorsConfiguration configuration = new CorsConfiguration();
        // below line is commented out. If uncommented, it would allow all origins.
        // configuration.setAllowedOrigin(Arrays.asList("*"));
        // This allows all origins using a wildcard "*".
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // This sets the allowed HTTP methods.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        // This sets the allowed HTTP headers.
        configuration
                .setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-XSRF-TOKEN"));
        // The line configuration.setAllowCredentials(true); is used to allow user
        // credentials to be included in the CORS request. This means that cookies,
        // authorization headers or TLS client certificates will be sent with the
        // request.

        // If you remove this line, the default behavior will be used, which is not to
        // include user credentials in the CORS request. This means that cookies,
        // authorization headers or TLS client certificates will not be sent with the
        // request. setAllowCredentials allows user credentials to be included in the
        // CORS request.
        configuration.setAllowCredentials(true);
        // Create a new UrlBasedCorsConfigurationSource object.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Register the CorsConfiguration for all endpoints under "/api/user/**".
        source.registerCorsConfiguration("/api/user/**", configuration);
        // Register the CorsConfiguration for all endpoints under "/api/notes/**".
        source.registerCorsConfiguration("/api/notes/**", configuration);
        // Return the CorsConfigurationSource object.
        return source;
    }

}

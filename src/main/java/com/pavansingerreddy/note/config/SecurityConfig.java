package com.pavansingerreddy.note.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    jsr250Enabled = true, // enables the JSR-250 standard java security annotations, like @RolesAllowed
    prePostEnabled = true   // enables the PreAuthorize and PostAuthorize annotations
)
public class SecurityConfig {


    @Autowired
    private JWTTokenFilter jwtTokenFilter;

    
    @Autowired
    JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    AuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    AccessDeniedHandler customAccessDeniedHandler;


    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(jwtAuthenticationProvider);
    }


    @Bean
    public SecurityFilterChain loginAndRegisterSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                // .csrf(csrf->csrf.disable())
                // .csrf(Customizer.withDefaults())
                .csrf((csrf) -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())   
				.csrfTokenRequestHandler(new SPACsrfTokenRequestHandler())   
                // .ignoringRequestMatchers("/api/user/csrf-token/**")         
			)
			.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth->{
                    auth.requestMatchers("/api/user/login/**").permitAll();
                    auth.requestMatchers("/api/user/register/**").permitAll();
                    auth.requestMatchers("/api/user/csrf-token/**").permitAll();
                    auth.requestMatchers("/api/user/verifyRegistration/**").permitAll();
                    auth.requestMatchers("/api/user/resendVerifyToken/**").permitAll();
                    auth.requestMatchers("/api/user/resetPassword/**").permitAll();
                    auth.requestMatchers("/api/user/verifyResetPassword/**").permitAll();
                    auth.requestMatchers("/api/user/isValidPasswordResetToken/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionHandling)->{
                    exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint);
                    exceptionHandling.accessDeniedHandler(customAccessDeniedHandler);
                })
                .logout((logout) -> logout
				// .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
                .logoutUrl("/api/user/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .deleteCookies("JWT")
                )
                .headers((headers)->{
                    headers.xssProtection(Customizer.withDefaults());
                    headers.contentSecurityPolicy(csp->csp.policyDirectives("script-src 'self'"));
                })
                // .cors(Customizer.withDefaults())
                .cors((cors)->cors.configurationSource(corsConfigurationSource()))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowedOrigin(Arrays.asList("*"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type","X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/user/register/**", configuration);
        source.registerCorsConfiguration("/api/user/login/**", configuration);
        source.registerCorsConfiguration("/api/user/**", configuration);
        source.registerCorsConfiguration("/api/notes/**", configuration);
        return source;
    }


}

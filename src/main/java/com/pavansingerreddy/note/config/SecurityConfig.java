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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pavansingerreddy.note.authentication_filter.JWTTokenFilter;
import com.pavansingerreddy.note.authentication_providers.JwtAuthenticationProvider;

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
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->{
                    auth.requestMatchers("/api/login/**").permitAll();
                    auth.requestMatchers("/api/register/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionHandling)->{
                    exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint);
                    exceptionHandling.accessDeniedHandler(customAccessDeniedHandler);
                })
                .cors(Customizer.withDefaults())
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowedOrigin(Arrays.asList("*"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/register/**", configuration);
        source.registerCorsConfiguration("/api/login/**", configuration);
        return source;
    }


}

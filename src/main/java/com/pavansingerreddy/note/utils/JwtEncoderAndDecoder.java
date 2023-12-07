package com.pavansingerreddy.note.utils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

// Configuration annotation tells Spring that this class contains one or more @Bean methods and may be processed by the Spring container to generate bean definitions and service requests for those beans at runtime.
@Configuration
// This class is used for defining the encoder and decoder for jwt's
public class JwtEncoderAndDecoder {

    // This annotation is used to inject the value of the property "jwt.public.key" from our application.yml
    // into the field "publicKey".
    @Value("${jwt.public.key}")
    // This is a field of type RSAPublicKey which will hold the public key.
    RSAPublicKey publicKey;

    // This annotation is used to inject the value of the property "jwt.private.key" from our application.yml
    // into the field "privateKey".
    @Value("${jwt.private.key}")
    // This is a field of type RSAPrivateKey which will hold the private key.
    RSAPrivateKey privateKey;

    // Bean annotation tells Spring that this method will return a bean that should
    // be managed by the Spring container.
    @Bean
    // This method returns a JwtDecoder object.
    public JwtDecoder jwtDecoder() {
        // It builds a JwtDecoder using the public key.
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    // Bean annotation tells Spring that this method will return a bean that should
    // be managed by the Spring container.
    @Bean
    // This method returns a JwtEncoder object.
    public JwtEncoder jwtEncoder() {
        // It builds a JWK (JSON Web Key) using the public and private keys.
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        // It creates a JWKSource from the JWK.
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        // It returns a JwtEncoder using the JWKSource.
        return new NimbusJwtEncoder(jwkSource);
    }
}

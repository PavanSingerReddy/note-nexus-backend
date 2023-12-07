package com.pavansingerreddy.note.utils;

import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;

// Component annotation tells Spring that this class is a component. It's a generic stereotype for any Spring-managed component.
@Component
// JWTUtil is a utility class for managing the jwt's
public class JWTUtil {

    // This Autowired annotation is used by Spring to automatically inject an
    // instantiated
    // bean of JwtEncoder into this field.
    @Autowired
    private JwtEncoder jwtEncoder;

    // This Autowired annotation is used by Spring to automatically inject an
    // instantiated
    // bean of JwtDecoder into this field.
    @Autowired
    private JwtDecoder jwtDecoder;

    // This annotation is used to inject the value of the property "jwt.public.key"
    // from the application.yml into the field "publicKey".
    @Value("${jwt.public.key}")
    // This is a field of type RSAPublicKey which will hold the public key.
    RSAPublicKey publicKey;
    // This annotation is used to inject the value of the property
    // "jwt.token.expiry.seconds" from application.yml into the field
    // "expireTimeInSeconds".
    @Value("${jwt.token.expiry.seconds}")
    // This is a field of type Long which will hold the expiry time of the jwt in
    // seconds.
    Long expireTimeInSeconds;

    // This method generates a JWT by using the authentication object.
    public String generateJwt(Authentication authentication) {

        // Get the current time.
        Instant now = Instant.now();
        // Calculate the expiry time by adding the expireTimeInSeconds to the current
        // time.
        Instant expiry = now.plus(Duration.ofSeconds(expireTimeInSeconds));
        // Get the authorities from the authentication object,
        String scope = authentication.getAuthorities().stream()
                // get the authority from each GrantedAuthority,
                .map(GrantedAuthority::getAuthority)
                // and join them into a string with a space separator.
                .collect(Collectors.joining(" "));

        // Start building a JwtClaimsSet,
        JwtClaimsSet claims = JwtClaimsSet.builder()
                // set the issuer,
                .issuer("notes-app")
                // set the issued at time,
                .issuedAt(now)
                // set the expiry time,
                .expiresAt(expiry)
                // set the subject with the name from the authentication object,
                .subject(authentication.getName())
                // and set a claim for the roles with the scope.
                .claim("roles", scope)
                // Build the JwtClaimsSet.
                .build();

        // Encode the JwtClaimsSet into a JWT and get the token value.
        String encodedJwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        // Return the encoded JWT.
        return encodedJwt;
    }

    // This method validates a JWT by taking the encoded jwt string.
    public boolean validateJwt(String encodedJwt) {
        SignedJWT signedJWT;
        // initially setting the isVerified to false and we will change it according to
        // our jwt validation
        boolean isVerified = false;
        try {
            // Parse the encoded JWT into a SignedJWT.
            signedJWT = SignedJWT.parse(encodedJwt);
            // The line JWSVerifier verifier = new RSASSAVerifier(this.publicKey); is
            // creating a new instance of JWSVerifier using the RSA public key.

            // JWSVerifier is an interface for classes that can verify JSON Web Signatures
            // (JWS). RSASSAVerifier is a concrete implementation of this interface that can
            // verify JWSs using RSA signatures.

            // In this case, new RSASSAVerifier(this.publicKey) is creating a new
            // RSASSAVerifier with the RSA public key (this.publicKey). This verifier can
            // then be used to verify the signature of a JWS, ensuring that the data has not
            // been tampered with and was signed by the expected party.

            // If the signature does not match, the verify() method of JWSVerifier will
            // return false, indicating a failed verification. If the signature matches, it
            // will return true, indicating a successful verification. This is crucial for
            // ensuring the integrity and authenticity of the JWT.
            JWSVerifier verifier = new RSASSAVerifier(this.publicKey);
            // Verify the SignedJWT with the verifier.
            isVerified = signedJWT.verify(verifier);
            // Decode the encoded JWT into a Jwt.
            Jwt jwt = jwtDecoder.decode(encodedJwt);
            // Get the claims from the Jwt.
            Map<String, Object> claimString = jwt.getClaims();
            // Get the expiry time from the claims.
            Instant jwtExpiry = (Instant) claimString.get("exp");
            // Check if the JWT is not expired.
            boolean isNotExpired = jwtExpiry.getEpochSecond() >= Instant.now().getEpochSecond();
            // Check if the subject in the claims is not null.subject contains the user's
            // email
            boolean userIsNotNull = claimString.get("sub") != null
                    &&
                    !claimString.get("sub").toString().equals(""); // and is not an empty string.
            // Return true if the JWT is verified, is not expired, and the user is not null
            // or empty.
            return isVerified && isNotExpired && userIsNotNull;
        } catch (Exception e) {
            // If there's an exception, return false.
            return false;
        }

    }

    // This method gets the username from a JWT token.
    public String getUsername(String token) {
        // Decode the token into a Jwt.
        Jwt jwt = jwtDecoder.decode(token);
        // Get the claims from the Jwt.
        Map<String, Object> claimString = jwt.getClaims();
        // Get the subject from the claims and convert it to a string.
        String username = claimString.get("sub").toString();
        // Return the username.
        return username;
    }

    // This method gets the roles from a JWT.
    public List<SimpleGrantedAuthority> getRoles(String token) {
        // Decode the token into a Jwt.
        Jwt jwt = jwtDecoder.decode(token);
        // Get the claims from the Jwt.
        Map<String, Object> claimString = jwt.getClaims();
        // Get the roles from the claims, convert it to a string, create a new
        // SimpleGrantedAuthority with it, and put it in a list.
        List<SimpleGrantedAuthority> roles = Arrays
                .asList(new SimpleGrantedAuthority(claimString.get("roles").toString()));
        // Return the roles.
        return roles;
    }
}

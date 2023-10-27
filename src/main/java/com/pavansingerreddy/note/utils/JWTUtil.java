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

@Component
public class JWTUtil {
     @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Value("${jwt.public.key}")
    RSAPublicKey publicKey;

    @Value("${jwt.token.expiry.seconds}")
    Long expireTimeInSeconds;


    public String generateJwt(Authentication authentication){

        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofSeconds(expireTimeInSeconds));

        String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                            .issuer("cloudnotes")
                            .issuedAt(now)
                            .expiresAt(expiry)
                            .subject(authentication.getName())
                            .claim("roles", scope)
                            .build();
        String encodedJwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return encodedJwt;
    }


    public boolean validateJwt(String encodedJwt){
        SignedJWT signedJWT;
        boolean isVerified = false;
        try {
            signedJWT = SignedJWT.parse(encodedJwt);
            JWSVerifier verifier = new RSASSAVerifier(this.publicKey);
            isVerified = signedJWT.verify(verifier);
            Jwt jwt = jwtDecoder.decode(encodedJwt);
            Map<String, Object> claimString = jwt.getClaims();
            Instant jwtExpiry = (Instant)claimString.get("exp");
            boolean isNotExpired = jwtExpiry.getEpochSecond() >= Instant.now().getEpochSecond();
    
            boolean userIsNotNull = claimString.get("sub") !=null 
            &&
            ! claimString.get("sub").toString().equals("");
            return isVerified && isNotExpired && userIsNotNull;
        } catch (Exception e) {
            return false;
        }
        
    }

    public String getUsername(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        Map<String, Object> claimString = jwt.getClaims();
        String username = claimString.get("sub").toString();
        return username;
    }

    public List <SimpleGrantedAuthority> getRoles(String token){
        Jwt jwt = jwtDecoder.decode(token);
        Map<String, Object> claimString = jwt.getClaims();
        List<SimpleGrantedAuthority> roles = 
        Arrays.asList(new SimpleGrantedAuthority(claimString.get("roles").toString()));
        return roles;
    }
}

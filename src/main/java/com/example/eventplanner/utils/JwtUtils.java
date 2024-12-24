package com.example.eventplanner.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // Method to generate token for the user based on email and role
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)  // Set the email as the subject
                .claim("role", role)  // Set the role as a custom claim
                .setIssuedAt(new Date())  // Set issue time
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // Set expiration time
                .signWith(SignatureAlgorithm.HS512, secretKey)  // Sign the token with the secret key
                .compact();  // Generate the token
    }

    // Extract the role from the JWT token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));  // Extract role from the claims
    }

    // Extract the email from the JWT token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);  // Extract email (subject) from the token
    }

    // Validate the token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);  // Verify token signature
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Helper method to extract claims
    private <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    // Helper method to extract all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // Functional interface to resolve claims
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}

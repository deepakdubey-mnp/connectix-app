package com.example.usermanagement.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    
    private final Key key;
    
    @Value("${jwt.expiration:86400000}") // Default: 1 day in milliseconds
    private long expiration;

    public JwtUtil() {
        // Generate a secure key for HS256
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(String username, String role) {
        log.debug("Generating JWT token for user: {} with role: {}", username, role);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error extracting claims from token", e);
            throw e;
        }
    }
    
    public boolean validateToken(String token, String username) {
        try {
            Claims claims = extractClaims(token);
            boolean isValid = claims.getSubject().equals(username) && !isTokenExpired(token);
            log.debug("Token validation for user {}: {}", username, isValid);
            return isValid;
        } catch (Exception e) {
            log.warn("Token validation failed for user: {}", username, e);
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            log.warn("Error checking token expiration", e);
            return true;
        }
    }
    
    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }
}

package ec.edu.ups.academic_events_api.security.utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import ec.edu.ups.academic_events_api.security.config.JwtProperties;
import ec.edu.ups.academic_events_api.security.services.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;

        this.signingKey = Keys.hmacShaKeyFor(
                properties.secret()
                        .getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(
            UserDetailsImpl user) {
        Instant now = Instant.now();

        Instant expiration = now.plusMillis(
                properties.accessExpiration());

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isValid(
            String token,
            UserDetailsImpl user) {
        try {
            String email = extractEmail(token);

            return email.equalsIgnoreCase(user.getUsername())
                    && extractExpiration(token)
                            .after(new Date())
                    && user.isEnabled();

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
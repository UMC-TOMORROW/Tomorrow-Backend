package com.umc.tomorrow.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            Date exp = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return exp.before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true; // 만료된 경우 true
        }
    }

    public String createJwt(Long id, String name, String username, String role, Long expiredMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiredMs);

        return Jwts.builder()
                .claim("id", id)
                .claim("name", name)
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)                 // 0.11.x
                .setExpiration(exp)               // 0.11.x
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 기존 시그니처 (호환)
    public String createJwt(Long id, String name, Long expiredMs) {
        return createJwt(id, name, null, null, expiredMs);
    }

    // Refresh Token (username만 포함)
    public String createRefreshToken(Long id, String username, Long expiredMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiredMs);

        return Jwts.builder()
                .setSubject("refresh")
                .claim("id", id)
                .claim("username", username)
                .setIssuedAt(now)                 // 0.11.x
                .setExpiration(exp)               // 0.11.x
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
    }

    public String getName(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("name", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        return Long.valueOf(getClaims(token).get("id").toString());
    }
}

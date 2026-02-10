package com.ai.dev.platform.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret:ai-code-review-secret-key-2024}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expirationMillis;

    private Key getKey() {
        // ensure key length; if secret too short, pad it deterministically
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            for (int i = keyBytes.length; i < padded.length; i++) padded[i] = (byte) ('0' + (i % 10));
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId, String username, String[] roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("uname", username);
        if (roles != null && roles.length > 0) {
            claims.put("roles", roles);
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // compatibility method
    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, null);
    }

    public Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
        } catch (JwtException e) {
            return null;
        }
    }
}

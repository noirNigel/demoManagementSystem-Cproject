package org.example.demomanagementsystemcproject.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key signingKey;
    private final String secret;
    // 过期时间：24 小时
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    public JwtUtil(@Value("${jwt.secret:}") String secret) {
        if (secret == null || secret.isBlank()) {
            // Generate a secure random key so the app can still start in local environments
            signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            this.secret = Encoders.BASE64.encode(signingKey.getEncoded());
            log.warn("No jwt.secret configured; generated a temporary signing key. Tokens will be invalid after restart.");
        } else {
            byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (secretBytes.length < 32) {
                throw new IllegalStateException("jwt.secret must be at least 32 bytes long to sign HS256 tokens.");
            }
            signingKey = Keys.hmacShaKeyFor(secretBytes);
            this.secret = secret;
        }
    }

    private Key getSigningKey() {
        return signingKey;
    }

    // 生成 token
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ⭐ 和 JwtFilter 里的方法名一致
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // ⭐ 和 JwtFilter 里的方法名一致
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 这里可以加日志
            return false;
        }
    }
}

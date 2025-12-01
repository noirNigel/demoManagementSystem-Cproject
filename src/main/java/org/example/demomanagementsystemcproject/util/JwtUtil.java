package org.example.demomanagementsystemcproject.util;

import io.jsonwebtoken.*;
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

    private static final String DEFAULT_SECRET = "this_is_a_demo_management_system_cproject_jwt_secret_key_2025!";

    private final Key signingKey;
    private final String secret;
    // 过期时间：24 小时
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    public JwtUtil(@Value("${jwt.secret:${JWT_SECRET:}}") String configuredSecret) {
        this.secret = (configuredSecret == null || configuredSecret.isBlank())
                ? DEFAULT_SECRET
                : configuredSecret.trim();

        byte[] secretBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret must be at least 32 bytes long to sign HS256 tokens. " +
                            "当前配置长度为 " + secretBytes.length + " 字节，请在 application.properties 或环境变量 JWT_SECRET 中配置更长的密钥。");
        }
        signingKey = Keys.hmacShaKeyFor(secretBytes);

        if (DEFAULT_SECRET.equals(this.secret)) {
            log.warn("使用默认开发密钥运行 JWT，建议在生产环境通过 jwt.secret 或 JWT_SECRET 配置自定义的长密钥。");
        } else {
            log.info("JWT 密钥初始化完成，使用外部配置的密钥。");
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

package com.gyy.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发/校验工具（HS256）。
 * 密钥来自配置 jwt.secret（生产从环境变量 JWT_SECRET 注入），须 ≥32 字节。
 * 见 docs/backend/04-业务逻辑文档.md §2。
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long ttlSeconds;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.ttl-seconds:7200}") long ttlSeconds) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret 长度须至少 32 字节（HS256 要求），当前 " + bytes.length);
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.ttlSeconds = ttlSeconds;
    }

    /** 签发 token：subject=userId，claim userName */
    public String createToken(Long userId, String userName) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userName", userName)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlSeconds * 1000))
                .signWith(key)
                .compact();
    }

    /** 校验并解析 token；非法/过期抛 JwtException 子类，由调用方决定错误码 */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }
}

package com.gyy.gateway.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * 网关侧 JWT 校验（HS256）。网关是 WebFlux 栈，不能依赖 gyy-common
 * （其携带 spring-boot-starter-web），因此本模块内实现轻量校验器。
 * 密钥与服务端保持一致：配置项 jwt.secret（环境变量 JWT_SECRET）。
 */
@Component
public class JwtVerifier {

    private final SecretKey key;

    public JwtVerifier(@Value("${jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret 长度须至少 32 字节（HS256 要求），当前 " + bytes.length);
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    /** 校验并解析；非法/过期抛 JwtException 子类 */
    public Claims verify(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}

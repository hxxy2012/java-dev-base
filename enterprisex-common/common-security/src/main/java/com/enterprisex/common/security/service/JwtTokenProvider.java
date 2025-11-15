package com.enterprisex.common.security.service;

import com.enterprisex.common.core.constant.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token 提供者
 *
 * @author EnterpriseX
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:YourBase64EncodedSecretKeyMinimum256BitsForHS256AlgorithmSecurity}")
    private String secret;

    /**
     * AccessToken过期时间（秒）默认2小时
     */
    @Value("${jwt.expiration:7200}")
    private Long expiration;

    /**
     * RefreshToken过期时间（秒）默认7天
     */
    @Value("${jwt.refresh-expiration:604800}")
    private Long refreshExpiration;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成AccessToken
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param authorities 权限列表
     */
    public String generateAccessToken(Long userId, String username, Object authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.JWT_USERID, userId);
        claims.put(Constants.JWT_USERNAME, username);
        claims.put(Constants.JWT_AUTHORITIES, authorities);
        claims.put(Constants.JWT_CREATED, new Date());

        return Jwts.builder()
                .subject(userId.toString())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成RefreshToken
     *
     * @param userId 用户ID
     */
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从Token中获取Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT解析失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(Constants.JWT_USERNAME, String.class);
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("JWT签名无效: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT参数为空: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT验证失败: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 从请求中获取Token
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
            return bearerToken.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 判断Token是否即将过期（剩余时间小于30分钟）
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            // 剩余时间小于30分钟
            return remainingTime < 30 * 60 * 1000;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get(Constants.JWT_USERNAME, String.class);
        Object authorities = claims.get(Constants.JWT_AUTHORITIES);

        return generateAccessToken(userId, username, authorities);
    }

    public Long getExpiration() {
        return expiration;
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }
}

package com.example.apigateway_service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        log.info("key:"+secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Redis 키 기반 Access Token 생성
    public JwtToken generateToken(String redisKey) {
        long now = System.currentTimeMillis();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 86400000); // 1일 유효
        String accessToken = Jwts.builder()
                .setSubject(redisKey) // Redis 키를 Subject로 설정
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(redisKey)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 604800000)) // 7일 유효
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // JWT 토큰 복호화
        Claims claims = parseClaims(accessToken);

        // 클레임에서 subject 추출 (Redis Key 사용)
        String redisKey = claims.getSubject(); // "sub" 값을 가져옴

        if (redisKey == null || redisKey.isEmpty()) {
            throw new RuntimeException("Redis Key가 없는 토큰입니다.");
        }

        // 권한 정보가 없으므로 빈 권한 리스트 생성
        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

        // UserDetails 객체 생성
        UserDetails principal = new User(redisKey, "", authorities);

        // Authentication 객체 생성 및 반환
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    // accessToken
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) // 서명 키 사용
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료된 토큰이라도 클레임은 추출 가능
        }
    }

    public String getSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // 서명 키
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // "sub" 값 반환
    }


}

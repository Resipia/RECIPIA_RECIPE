package com.recipia.recipe.hexagonal.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenUtils {

    // JWT 비밀 키, HMAC-SHA 알고리즘을 위한 키 생성
    private static final String jwtSecretKey = "thisIsASecretKeyUsedForJwtTokenGenerationAndItIsLongEnoughToMeetTheRequirementOf256Bits";
    private static final Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));


    // 액세스 토큰 유형 상수
    public static final String ACCESS_TOKEN_TYPE = "access";

    // 토큰에서 역할(role)을 가져오는 메서드
    public static String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    // 토큰에서 Claims 정보를 추출하는 메서드
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token).getBody();
    }

    // 토큰에서 Claims 정보를 맵 형태로 가져오는 메서드
    public static Map<String, Object> getClaimsMapFromToken(String token) {
        return getClaimsFromToken(token);
    }

    /**
     * "exp" 클레임을 Instant 타입으로 반환할 수 있는 메서드
     * 원인: JWT의 "exp" (유효기간) 클레임은 Instant 타입이어야 함. Integer로 설정하면 문제가 발생할 수 있음.
     * 해결: "exp" 클레임의 값을 Instant 타입으로 변환해야 함.
     */
    public static Instant getExpirationFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("exp", Date.class).toInstant();
    }

    // 토큰의 클레임에서 "memberId"를 꺼내는 메서드
    public static Long getMemberIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.get("memberId").toString());
    }

    // 토큰의 클레임에서 "nickname"을 꺼내는 메서드
    public static String getNicknameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("nickname").toString();
    }

    // 토큰에서 사용자 이름을 가져오는 메서드
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

}

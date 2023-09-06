package com.recipia.recipe.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenUtils {

    private static final String jwtSecretKey = "thisIsASecretKeyUsedForJwtTokenGenerationAndItIsLongEnoughToMeetTheRequirementOf256Bits";
    private static final Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    private static final String USERNAME = "username";
    private static final String NICKNAME = "nickname";
    private static final String ROLE = "role";
    public static final String ACCESS_TOKEN_TYPE = "access";


    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(USERNAME, String.class);
    }

    public static String getNicknameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(NICKNAME, String.class);
    }

    public static String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(ROLE, String.class);
    }

    /**
     * 토큰 정보를 기반으로 Claims 정보를 반환받는 메서드
     * @return Claims : Claims
     */
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody();
    }

}

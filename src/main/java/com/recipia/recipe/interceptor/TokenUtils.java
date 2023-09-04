package com.recipia.recipe.interceptor;

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
    public static final String ACCESS_TOKEN_TYPE = "access";


    /**
     * 토큰을 기반으로 사용자의 정보를 반환해주는 메서드
     */
    // todo:
    //  - 유효한 token인지 검증
    //  - claim에서 username, nickname 추출
    //  - MEMBER 서버에서 실제 존재하는 회원인지 검증
    public static boolean isValidToken(String token, String tokenType) {
        try {
            Claims claims = getClaimsFromToken(token);
            String type = claims.get("type", String.class);
            if (!type.equals(tokenType)) {
                return false;
            }
            log.info("isValidToken - Token is valid for username: " + claims.get(USERNAME));
            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("isValidToken - Token Expired", expiredJwtException);
            return false;
        } catch (JwtException jwtException) {
            log.error("isValidToken - Token Tampered", jwtException);
            return false;
        } catch (NullPointerException npe) {
            log.error("isValidToken - Token is null", npe);
            return false;
        }
    }

    /**
     * 토큰 정보를 기반으로 Claims 정보를 반환받는 메서드
     * @return Claims : Claims
     */
    private static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody();
    }


}

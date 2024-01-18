package com.recipia.recipe.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenValidator {

    /**
     * 토큰의 유효성을 검사하는 메서드
     * @param token 검사할 JWT 토큰
     * @param tokenType 토큰 유형 (예: "access")
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValidToken(String token, String tokenType) {
        try {
            Claims claims = TokenUtils.getClaimsFromToken(token);
            String type = claims.get("type", String.class);
            // 입력된 토큰 유형과 일치하는지 확인
            return type.equals(tokenType);
        } catch (JwtException jwtException) {
            log.debug("JWT validation error: {}", jwtException.getMessage());
            if (jwtException instanceof ExpiredJwtException) {
                log.debug("Expired JWT token: {}", token);
            } else if (jwtException instanceof MalformedJwtException) {
                log.debug("Malformed JWT token: {}", token);
            } else {
                log.debug("Other JWT error: {}", jwtException.getClass().getSimpleName());
            }
            return false;
        }
    }

}

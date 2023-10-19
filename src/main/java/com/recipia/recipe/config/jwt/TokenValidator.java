package com.recipia.recipe.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
            log.error("Invalid token: {}", jwtException.getMessage());
            return false;
        }
    }

}

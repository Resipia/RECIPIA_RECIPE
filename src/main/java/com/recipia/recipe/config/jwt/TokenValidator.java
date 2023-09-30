package com.recipia.recipe.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenValidator {

    public boolean isValidToken(String token, String tokenType) {
        try {
            Claims claims = TokenUtils.getClaimsFromToken(token);
            String type = claims.get("type", String.class);
            return type.equals(tokenType);
        } catch (JwtException jwtException) {
            return false;
        }
    }

}

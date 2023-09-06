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
            if (!type.equals(tokenType)) {
                return false;
            }
            String username = claims.get("username", String.class);
            // todo: kafka로 MEMBER 서버 통신
//            Member member = memberRepository.findMemberByUsernameAndStatus(username, MemberStatus.ACTIVE).orElse(null);
//            return member != null;
            return true;
        } catch (JwtException jwtException) {
            return false;
        }
    }

}

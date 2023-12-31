package com.recipia.recipe.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * 시큐리티에서 memberId, nickname을 꺼낼 수 있도록 하는 유틸 클래스
 */
public class SecurityUtil {

    public static Long getCurrentMemberId() {
        Jwt jwt = getCurrentJwt();
        return jwt != null ? jwt.getClaim("memberId") : null;
    }

    public static String getCurrentMemberNickname() {
        Jwt jwt = getCurrentJwt();
        return jwt != null ? jwt.getClaim("nickname") : null;
    }

    private static Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            // todo: 만약 null인 경우에는 시큐리티에서 처리하는지 알아보기
            // 인증되지 않은 상태 처리
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt) {
            return (Jwt) authentication.getPrincipal();
        }

        return null; // Principal이 Jwt 타입이 아닌 경우
    }

}

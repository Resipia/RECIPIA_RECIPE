package com.recipia.recipe.common.utils;

import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * 시큐리티에서 memberId, nickname을 꺼낼 수 있도록 하는 유틸 클래스
 */
public class SecurityUtil {

    /**
     * jwt에서 현재 사용하는 유저의 memberId를 꺼낸다.
     */
    public static Long getCurrentMemberId() {
        Jwt jwt = getCurrentJwt(); // 여기서 jwt는 null이 아님을 보장
        Long memberId = jwt.getClaim("memberId");
        if (memberId == null) {
            throw new RecipeApplicationException(ErrorCode.USER_NOT_FOUND); // memberId가 없는 경우 예외 처리
        }
        return memberId; // 정상적으로 memberId 반환
    }

    /**
     * jwt에서 현재 사용하는 유저의 nickname을 꺼낸다.
     */
    public static String getCurrentMemberNickname() {
        Jwt jwt = getCurrentJwt();
        return jwt != null ? jwt.getClaim("nickname") : null;
    }

    /**
     * 이 메서드는 SecurityContextHolder를 통해 현재 인증된 사용자의 Authentication 객체를 가져오고 이 객체가 Jwt 타입인 경우에 해당 Jwt 객체를 반환한다.
     * 만약 인증 정보가 없거나 유효하지 않은 경우 (null이거나 인증되지 않은 경우) RecipeApplicationException 예외를 발생시킨다.
     */
    private static Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RecipeApplicationException(ErrorCode.MISSING_JWT);
        }

        if (authentication.getPrincipal() instanceof Jwt) {
            return (Jwt) authentication.getPrincipal();
        }

        return null;
    }

}

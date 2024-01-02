package com.recipia.recipe.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("[단위] 시큐리티 유틸 클래스")
class SecurityUtilTest {

    @DisplayName("jwt에서 현재 사용하는 유저의 memberId를 꺼낸다.")
    @Test
    void getCurrentMemberIdTest() {
        //given
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("memberId")).thenReturn(12345L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // SecurityContextHolder에 모킹한 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);

        // when
        Long memberId = SecurityUtil.getCurrentMemberId();

        // then
        assertEquals(12345L, memberId);
    }


    @DisplayName("jwt에서 현재 사용하는 유저의 nickname을 꺼낸다.")
    @Test
    void getCurrentMemberNicknameTest() {
        // given
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("nickname")).thenReturn("userNickname");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // SecurityContextHolder에 모킹한 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);

        // when
        String nickname = SecurityUtil.getCurrentMemberNickname();

        // then
        assertEquals("userNickname", nickname);
    }

}
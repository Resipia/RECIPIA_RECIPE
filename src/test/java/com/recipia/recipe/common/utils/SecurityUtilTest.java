package com.recipia.recipe.common.utils;

import com.recipia.recipe.config.TestJwtConfig;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[통합] 시큐리티 유틸 클래스")
class SecurityUtilTest extends TotalTestSupport {

    @Autowired
    private SecurityUtil securityUtil;


    @DisplayName("jwt에서 현재 사용하는 유저의 memberId를 꺼낸다.")
    @Test
    void getCurrentMemberIdTest() {
        //given
        Long expectedMemberId = 12345L;
        String dummyNickname = "userNickname";
        TestJwtConfig.setupMockJwt(expectedMemberId, dummyNickname);

        // when
        Long memberId = securityUtil.getCurrentMemberId();

        // then
        assertEquals(12345L, memberId);
    }

    @DisplayName("jwt에서 현재 사용하는 유저의 nickname을 꺼낸다.")
    @Test
    void getCurrentMemberNicknameTest() {
        // given
        Long expectedMemberId = 12345L;
        String dummyNickname = "userNickname";
        TestJwtConfig.setupMockJwt(expectedMemberId, dummyNickname);

        // when
        String nickname = securityUtil.getCurrentMemberNickname();

        // then
        assertEquals("userNickname", nickname);
    }
}

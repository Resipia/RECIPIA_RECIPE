package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.application.port.in.MyPageUseCase;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.MyPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[통합] 마이페이지 컨트롤러 테스트")
@AutoConfigureMockMvc
class MyPageControllerTest extends TotalTestSupport {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MyPageUseCase myPageUseCase;
    @MockBean
    private SecurityUtil securityUtil;

    @DisplayName("[happy] 유저가 본인이 작성한 레시피 갯수를 요청하면 성공 응답을 반환한다.")
    @Test
    void getMyRecipeCount() throws Exception {
        // given
        Long memberId = 1L;
        MyPage domain = MyPage.of(1L);
        when(securityUtil.getCurrentMemberId()).thenReturn(memberId);
        when(myPageUseCase.getRecipeCount(memberId)).thenReturn(domain);

        //when & then
        mockMvc.perform(post("/recipe/mypage/recipeCnt")
                ).andExpect(status().isOk())
                .andDo(print());
    }

}
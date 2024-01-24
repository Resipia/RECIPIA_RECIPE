package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.domain.MyPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위] 마이페이지 서비스 테스트")
class MyPageServiceTest {

    @InjectMocks
    private MyPageService sut;
    @Mock
    private RecipePort recipePort;

    @DisplayName("[happy] 유효한 memberId가 들어오면 그 사용자가 작성한 레시피 갯수가 담겨있는 MyPage 도메인을 반환한다.")
    @Test
    void getRecipeCountSeuccess() {
        // given
        Long memberId = 1L;
        Long count = 1L;
        when(recipePort.getMyRecipeCount(memberId)).thenReturn(count);
        MyPage domain = MyPage.of(count);

        // when
        MyPage myPage = sut.getRecipeCount(memberId);
        // then
        assertEquals(domain.getRecipeCount(), myPage.getRecipeCount());

    }

}
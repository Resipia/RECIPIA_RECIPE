package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.MyPageUseCase;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.domain.MyPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 마이페이지 서비스 클래스
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MyPageService implements MyPageUseCase {

    private final RecipePort recipePort;

    /**
     * [READ] memberId에 해당하는 회원이 작성한 레시피 갯수를 가져온다.
     */
    @Override
    public MyPage getRecipeCount(Long memberId) {
        Long myRecipeCount = recipePort.getMyRecipeCount(memberId);
        return MyPage.of(myRecipeCount);
    }
}

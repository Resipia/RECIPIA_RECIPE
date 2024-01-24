package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.MyPage;

public interface MyPageUseCase {
    MyPage getRecipeCount(Long memberId);
}

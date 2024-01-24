package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.domain.MyPage;

import java.util.List;

public interface MyPageUseCase {
    MyPage getRecipeCount(Long memberId);
    List<RecipeMainListResponseDto> getMyRecipeHigh(Long memberId);
}

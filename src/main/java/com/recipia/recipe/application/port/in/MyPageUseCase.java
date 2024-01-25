package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.domain.MyPage;

import java.util.List;

public interface MyPageUseCase {
    MyPage getRecipeCount(Long targetMemberId);
    List<RecipeListResponseDto> getTargetMemberRecipeHigh(Long targetMemberId);
    PagingResponseDto<RecipeListResponseDto> getTargetMemberRecipeList(int page, int size, String sortType, Long targetMemberId);
    PagingResponseDto<RecipeListResponseDto> getAllMyBookmarkList(int page, int size);
    PagingResponseDto<RecipeListResponseDto> getAllMyLikeList(int page, int size);

}

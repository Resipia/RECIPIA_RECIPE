package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;

public interface ReadRecipeUseCase {

    PagingResponseDto<RecipeMainListResponseDto> getAllRecipeList(int page, int size, String sortType);

    RecipeDetailViewDto getRecipeDetailView(Long recipeId);
}

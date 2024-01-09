package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.domain.Recipe;

public interface ReadRecipeUseCase {

    PagingResponseDto<RecipeMainListResponseDto> getAllRecipeList(int page, int size, String sortType);

    Recipe getRecipeDetailView(Recipe domain);
}

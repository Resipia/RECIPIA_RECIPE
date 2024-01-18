package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.domain.Recipe;

import java.util.List;

public interface ReadRecipeUseCase {

    PagingResponseDto<RecipeMainListResponseDto> getAllRecipeList(int page, int size, String sortType, List<Long> subCategoryList);

    Recipe getRecipeDetailView(Recipe domain);
}

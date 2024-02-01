package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.domain.Recipe;

import java.util.List;

public interface ReadRecipeUseCase {

    PagingResponseDto<RecipeListResponseDto> getAllRecipeList(int page, int size, String sortType, List<Long> subCategoryList, String searchWord);

    Recipe getRecipeDetailView(Recipe domain);
}

package com.recipia.recipe.application.port.out;


import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * adapter 클래스의 인터페이스
 */
public interface RecipePort {

    Long updateRecipesNicknames(NicknameDto nicknameDto);

    Long createRecipe(Recipe recipe);

    Long createNutritionalInfo(Recipe recipe, Long savedRecipeId);

    void createRecipeCategoryMap(Recipe recipe, Long savedRecipeId);

    Page<RecipeMainListResponseDto> getAllRecipeList(Pageable pageable, String sortType);

    RecipeDetailViewDto getRecipeDetailView(Long recipeId);

    List<Long> saveRecipeFile(List<RecipeFile> recipeFile);
}

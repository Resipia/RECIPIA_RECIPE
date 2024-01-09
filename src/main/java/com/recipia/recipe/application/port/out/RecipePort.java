package com.recipia.recipe.application.port.out;


import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import com.recipia.recipe.domain.SubCategory;
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

    Recipe getRecipeDetailView(Long recipeId);

    List<SubCategory> getSubCategories(Long recipeId);

    List<Long> saveRecipeFile(List<RecipeFile> recipeFile);

    Long updateRecipe(Recipe recipe);

    void updateNutritionalInfo(Recipe recipe);

    void updateCategoryMapping(Recipe recipe);

    Long softDeleteRecipeFilesByRecipeId(Long recipeId);

    NutritionalInfo getNutritionalInfo(Long recipeId);

    List<RecipeFile> getRecipeFile(Long recipeId);

    Long softDeleteRecipeByRecipeId(Long recipeId);
}

package com.recipia.recipe.application.port.out;


import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
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

    Long createRecipe(Recipe recipe);

    Long createNutritionalInfo(Recipe recipe, Long savedRecipeId);

    void createRecipeCategoryMap(Recipe recipe, Long savedRecipeId);

    Page<RecipeListResponseDto> getAllRecipeList(Pageable pageable, String sortType, List<Long> subCategoryList);

    Recipe getRecipeDetailView(Recipe domain);

    List<SubCategory> getSubCategories(Long recipeId);

    List<Long> saveRecipeFile(List<RecipeFile> recipeFile);

    Long updateRecipe(Recipe recipe);

    void updateNutritionalInfo(Recipe recipe);

    void updateCategoryMapping(Recipe recipe);

    NutritionalInfo getNutritionalInfo(Long recipeId);

    List<RecipeFile> getRecipeFileList(Long recipeId);

    Long softDeleteByRecipeId(Recipe domain);

    boolean checkIsRecipeMineExist(Recipe recipe);

    boolean checkIsRecipeExist(Recipe recipe);

    Integer findMaxFileOrder(Long savedRecipeId);

    Long softDeleteRecipeFile(Recipe domain, List<Integer> deleteFileOrder);

    Long softDeleteRecipeFilesInRecipeIds(List<Long> recipeIds);

    Long deleteNutritionalInfosInRecipeIds(List<Long> recipeIds);

    Long deleteRecipeCategoryMapsInRecipeIds(List<Long> recipeIds);

    List<Long> getAllRecipeIdsByMemberId(Long memberId);

    Long softDeleteRecipeByMemberId(Long memberId);
}

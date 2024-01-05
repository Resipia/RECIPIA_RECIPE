package com.recipia.recipe.application.port.out;


import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * adapter 클래스의 인터페이스
 */
public interface RecipePort {

    Long updateRecipesNicknames(NicknameDto nicknameDto);

    Long createRecipe(Recipe recipe);

    Long createNutritionalInfo(Recipe recipe, Long savedRecipeId);

    void createRecipeCategoryMap(Recipe recipe, Long savedRecipeId);

    Page<RecipeMainListResponseDto> getAllRecipeList(Pageable pageable, String sortType);
}

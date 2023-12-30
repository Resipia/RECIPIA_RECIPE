package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Recipe;

import java.util.List;

public interface CreateRecipeUseCase {

    // 레시피 생성
    Long createRecipe(Recipe recipe);

}

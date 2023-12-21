package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Recipe;

public interface CreateRecipeUseCase {

    Long createRecipe(Recipe recipe);

}

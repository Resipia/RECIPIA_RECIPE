package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Recipe;

public interface DeleteRecipeUseCase {
    Long deleteRecipeByRecipeId(Recipe domain);
}

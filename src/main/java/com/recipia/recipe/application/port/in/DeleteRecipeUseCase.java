package com.recipia.recipe.application.port.in;

public interface DeleteRecipeUseCase {
    Long deleteRecipeByRecipeId(Long recipeId);
}

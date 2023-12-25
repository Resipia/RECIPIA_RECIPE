package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Recipe;
import reactor.core.publisher.Mono;

public interface CreateRecipeUseCase {

    Long createRecipe(Recipe recipe);

    Mono<Void> saveIngredientsIntoMongo();
}

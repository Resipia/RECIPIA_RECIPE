package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.application.port.in.DeleteRecipeUseCase;
import com.recipia.recipe.application.port.in.ReadRecipeUseCase;
import com.recipia.recipe.application.port.in.UpdateRecipeUseCase;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import com.recipia.recipe.domain.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeService implements CreateRecipeUseCase, ReadRecipeUseCase, UpdateRecipeUseCase, DeleteRecipeUseCase {

    private final RecipePort recipePort;
    private final ApplicationEventPublisher eventPublisher;

    // 레시피 생성
    @Override
    public Long createRecipe(Recipe recipe) {

        Long savedRecipeId = recipePort.createRecipe(recipe);
        eventPublisher.publishEvent(new RecipeCreationEvent(recipe.getIngredient(), recipe.getHashtag()));

        return savedRecipeId;
    }

    // mongoDB에 재료를 저장한다.
    @Override
    public void saveIngredientsIntoMongo() {

//        recipePort.saveIngredientsIntoMongo();

    }


}

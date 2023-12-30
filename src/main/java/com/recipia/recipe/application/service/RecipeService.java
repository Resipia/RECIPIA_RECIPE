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

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeService implements CreateRecipeUseCase, ReadRecipeUseCase, UpdateRecipeUseCase, DeleteRecipeUseCase {

    private final RecipePort recipePort;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 레시피 생성을 담당하는 메서드
     * 주관심사: 레시피 생성 (엔티티 저장)
     * 비관심사: 스프링 이벤트 발행 (재료, 해시태그 mongoDB에 저장)
     */
    @Override
    public Long createRecipe(Recipe recipe) {
        Long savedRecipeId = recipePort.createRecipe(recipe);
        eventPublisher.publishEvent(new RecipeCreationEvent(recipe.getIngredient(), recipe.getHashtag()));

        return savedRecipeId;
    }

}

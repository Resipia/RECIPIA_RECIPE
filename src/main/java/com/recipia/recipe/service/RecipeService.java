package com.recipia.recipe.service;

import com.diningtalk.recipe.domain.Recipe;
import com.diningtalk.recipe.domain.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    /**
     * 레시피 저장
     */
    public Recipe saveRecipe(Recipe recipe) {

        return Optional.of(recipeRepository.save(recipe))
                    .orElseThrow(
                            () -> new IllegalArgumentException("userId, recipe name is required")
                    );
    }
}



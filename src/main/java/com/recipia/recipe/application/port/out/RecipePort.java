package com.recipia.recipe.application.port.out;


import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.domain.Recipe;

import java.util.List;

/**
 * adapter 클래스의 인터페이스
 */
public interface RecipePort {

    Long updateRecipesNicknames(NicknameDto nicknameDto);

    Long createRecipe(Recipe recipe);

    Long saveIngredientsIntoMongo(List<String> newIngredients);
}

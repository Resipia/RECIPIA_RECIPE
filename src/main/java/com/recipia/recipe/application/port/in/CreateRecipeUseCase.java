package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Recipe;

import java.util.List;

public interface CreateRecipeUseCase {

    // 레시피 생성
    Long createRecipe(Recipe recipe);

    // 스프링 이벤트 리스너가 사용: 몽고 db에 재료 저장
    void saveIngredientsIntoMongo(List<String> ingredients);
}

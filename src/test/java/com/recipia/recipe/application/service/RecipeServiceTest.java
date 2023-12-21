package com.recipia.recipe.application.service;

import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Recipe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("레시피 서비스 테스트")
class RecipeServiceTest extends TotalTestSupport {

    @Autowired private RecipeService sut;

    @DisplayName("[happy] - 유저가 레시피 저장에 성공하면 저장된 레시피 엔티티의 Id값을 반환한다.")
    @Test
    void recipeCreateServiceTest() {
        //given
        Recipe recipe = createRecipe();

        //when
        Long result = sut.createRecipe(recipe);

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(recipe.getId());
    }

    private Recipe createRecipe() {
        return Recipe.of(10L, 1L, "레시피", "레시피 설명", 20, "닭", "#닭발", "{당류: 많음}", "진안", "N");
    }

}
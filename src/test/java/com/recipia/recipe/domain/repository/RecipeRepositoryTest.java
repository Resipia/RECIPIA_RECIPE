package com.recipia.recipe.domain.repository;

import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Recipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("recipe-repo-test")
class RecipeRepositoryTest extends TotalTestSupport {

    @Autowired
    private RecipeRepository recipeRepository;

    @DisplayName("레시피 저장 테스트")
    @Test
    void recipeSaveTest() {
        //given
        Recipe recipe = createRecipe();
        //when
        Recipe savedRecipe = recipeRepository.save(recipe);
        //then
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getMemberId()).isEqualTo(recipe.getMemberId());

    }

    private Recipe createRecipe() {
        return Recipe.of(1L, 1L, "recipe-name", "recipe-desc", 30, "yjkim", "N");

    }

}
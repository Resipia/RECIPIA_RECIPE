package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import com.recipia.recipe.config.TestSecurityConfig;
import com.recipia.recipe.config.TestZipkinConfig;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Recipe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("[단위] 레시피 서비스 테스트")
class RecipeServiceTest {

    @Mock
    private RecipePort recipePort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RecipeService sut;

    @DisplayName("[happy] - 레시피 저장 시 저장된 레시피 ID를 반환하고 이벤트를 발생시킨다.")
    @Test
    void createRecipe_Success() {
        // given
        Recipe recipe = createRecipe();
        Long savedRecipeId = 10L;  // 가정하는 저장된 ID

        // RecipePort의 동작을 정의
        when(recipePort.createRecipe(recipe)).thenReturn(savedRecipeId);

        // when
        Long result = sut.createRecipe(recipe);

        // then
        assertThat(result).isEqualTo(savedRecipeId);
        then(eventPublisher).should().publishEvent(new RecipeCreationEvent(recipe.getIngredient(), recipe.getHashtag()));
    }

    private Recipe createRecipe() {
        return Recipe.of(10L, 1L, "레시피", "레시피 설명", 20, "닭", "#닭발", "{당류: 많음}", "진안", "N");
    }

}
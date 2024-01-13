package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.out.RecipeLikePort;
import com.recipia.recipe.application.port.out.RedisPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeLike;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 목 객체 초기화
@DisplayName("[단위] 좋아요 서비스 테스트")
class RecipeLikeAndViewServiceTest {

    @Mock
    private RedisPort redisPort;

    @Mock
    private RecipeLikePort recipeLikePort;

    @InjectMocks
    private RecipeLikeAndViewService sut;

    @Test
    @DisplayName("[happy] 좋아요 저장 및 레디스 카운트 증가")
    void testRecipeLikeCreation() {
        //given
        RecipeLike domain = RecipeLike.of(Recipe.of(1L), 1L);
        when(recipeLikePort.saveLike(any(RecipeLike.class))).thenReturn(1L);

        //when
        Long savedId = sut.recipeLikeProcess(domain);
        verify(recipeLikePort).saveLike(any(RecipeLike.class));
        verify(redisPort).incrementLikeCount(1L);
        assertEquals(1L, savedId);
    }

    @Test
    @DisplayName("[happy] 좋아요 삭제 및 레디스 카운트 감소")
    void testRecipeLikeDeletion() {
        //given
        RecipeLike domain = RecipeLike.of(1L, Recipe.of(1L), 1L);

        //when
        Long resultId = sut.recipeLikeProcess(domain);

        //then
        verify(recipeLikePort).deleteRecipeLike(domain);
        verify(redisPort).decreaseLikeCount(1L);
        assertEquals(0L, resultId);
    }

    @Test
    @DisplayName("[bad] 연관된 레시피가 없는데 좋아요 저장을 시도하면 예외가 발생한다.")
    void testRecipeLikeCreationException() {
        //given
        RecipeLike domain = RecipeLike.of(Recipe.of(1L), 1L);
        when(recipeLikePort.saveLike(any(RecipeLike.class))).thenThrow(new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND));

        //when
        Assertions.assertThatThrownBy(() -> sut.recipeLikeProcess(domain))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    @Test
    @DisplayName("[bad] 연관된 레시피가 없는데 좋아요 삭제를 시도하면 예외가 발생한다.")
    void testRecipeLikeDeletionException() {
        //given
        RecipeLike domain = RecipeLike.of(1L, Recipe.of(1L), 1L);
        // void를 반환하면 doThrow로 작성해야함
        doThrow(new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND)).when(recipeLikePort).deleteRecipeLike(any(RecipeLike.class));

        //when
        Assertions.assertThatThrownBy(() -> sut.recipeLikeProcess(domain))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

}
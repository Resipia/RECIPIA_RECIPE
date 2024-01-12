package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeLike;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[통합] 레시피 queryDsl 테스트")
class RecipeLikeAdapterTest extends TotalTestSupport {

    @Autowired
    private RecipeLikeAdapter sut;

    @Autowired
    private RecipeLikeRepository recipeLikeRepository;
    
    

    @DisplayName("[happy] 좋아요에 성공하면 저장된 좋아요 id값을 반환한다.")
    @Test
    void saveLikeTest() {
        //given
        RecipeLike domain = RecipeLike.of(1L, Recipe.of(1L), 1L);

        //when
        Long resultCount = sut.saveLike(domain);

        //then
        Assertions.assertThat(resultCount).isNotNull();
        Assertions.assertThat(resultCount).isGreaterThan(0);
    }

    @DisplayName("[bad] 존재하지 않는 레시피 id에 좋아요 저장을 시도하면 예외가 발생한다.")
    @Test
    void saveLikeException() {
        //given
        RecipeLike domain = RecipeLike.of(1L, Recipe.of(9999L), 1L);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.saveLike(domain))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    @DisplayName("[happy] 좋아요를 삭제한후 그 id로 조회하면 데이터가 존재하지 않는다는 결과를 받을 수 있다.")
    @Test
    void deleteRecipeLikeTest() {
        //given
        RecipeLike domain = RecipeLike.of(Recipe.of(1L), 1L);
        Long savedId = sut.saveLike(domain);
        RecipeLike savedDomain = RecipeLike.of(savedId, Recipe.of(1L), 1L);

        //when
        sut.deleteRecipeLike(savedDomain);

        //then
        boolean exists = recipeLikeRepository.existsById(savedId);
        Assertions.assertThat(exists).isFalse();
    }

    @DisplayName("[bad] 존재하지 않는 레시피id로 좋아요 삭제를 시도하면 예외가 발생한다.")
    @Test
    void deleteRecipeLikeTestException1() {
        //given
        RecipeLike domain = RecipeLike.of(Recipe.of(1L), 1L);
        Long savedId = sut.saveLike(domain);
        RecipeLike savedDomain = RecipeLike.of(savedId, Recipe.of(100L), 1L);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.deleteRecipeLike(savedDomain))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    @DisplayName("[bad] 레시피는 존재하지만 좋아요id는 존재하지 않은 상태로 좋아요 삭제를 시도하면 예외가 발생한다.")
    @Test
    void deleteRecipeLikeTestException2() {
        //given
        RecipeLike domain = RecipeLike.of(Recipe.of(1L), 1L);
        Long savedId = sut.saveLike(domain);
        RecipeLike savedDomain = RecipeLike.of(100L, Recipe.of(1L), 1L);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.deleteRecipeLike(savedDomain))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("좋아요가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_LIKE);
    }

}
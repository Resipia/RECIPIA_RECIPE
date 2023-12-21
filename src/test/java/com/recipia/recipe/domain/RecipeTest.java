package com.recipia.recipe.domain;

import com.recipia.recipe.common.exception.RecipeApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Recipe 도메인 테스트 (SpringBoot에 연결할 필요가 없다.)
 */
class RecipeTest {

    @DisplayName("[happy] - 레시피를 등록할 때 모든 데이터를 제대로 넣어주면 예외가 발생하지 않는다.")
    @Test
    void createRecipeSuccess() {

        //given
        Recipe recipe = createRecipe(1L, "맛도리 닭갈비", "맛도리 닭갈비", "진안", "N");

        //when
        recipe.validateBasicInfo();

        //then
        // 예외가 발생하지 않으면 테스트 통과
    }

    @DisplayName("[bad] - 레시피를 등록할때 잘못된 입력값이 들어오면 예외가 발생하고 커스텀 ERROR 메시지를 보여준다.")
    @ParameterizedTest
    @MethodSource("invalidRecipeProvider")
    void shouldThrowExceptionForInvalidInputs(Long memberId, String recipeName, String recipeDesc, String nickname, String delYn,
                                              Class<?> expectedException, String expectedMessage) {
        //given
        Recipe recipe = Recipe.createTest(memberId, recipeName, recipeDesc, nickname, delYn);

        //when & then
        RecipeApplicationException exception = assertThrows((Class<RecipeApplicationException>) expectedException, recipe::validateBasicInfo);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidRecipeProvider() {
        return Stream.of(
                Arguments.of(null, "맛도리 닭갈비", "맛도리 닭갈비", "진안", "N", RecipeApplicationException.class, "회원 ID는 필수 항목입니다."),
                Arguments.of(1L, null, "맛도리 닭갈비", "진안", "N", RecipeApplicationException.class, "레시피 이름은 필수 항목입니다."),
                Arguments.of(1L, "맛도리 닭갈비", null, "진안", "N", RecipeApplicationException.class, "레시피 설명은 필수 항목입니다."),
                Arguments.of(1L, "맛도리 닭갈비", "맛도리 닭갈비", null, "N", RecipeApplicationException.class, "닉네임은 필수 항목입니다."),
                Arguments.of(1L, "맛도리 닭갈비", "맛도리 닭갈비", "진안", null, RecipeApplicationException.class, "삭제 여부는 필수 항목입니다.")
                // ... [기타 테스트 케이스들] ...
        );
    }

    private Recipe createRecipe(Long memberId, String recipeName, String recipeDesc, String nickname, String delYn) {
        return Recipe.createTest(
                memberId,
                recipeName,
                recipeDesc,
                nickname,
                delYn
        );
    }


}
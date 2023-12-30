package com.recipia.recipe.adapter.in.web.dto;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("레시피 저장/업데이트에 사용되는 RequestDto 테스트")
class RecipeRequestDtoTest {

    // Java의 Bean Validation API를 사용해 유효성 검증을 수행하기 위한 Validator 객체를 생성
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    @DisplayName("[happy] 모든 유효성 검사를 통과하는 경우")
    void whenValidRequest_thenNoValidationErrors() {
        RecipeRequestDto dto = RecipeRequestDto.of("닭갈비", "세상에서 제일 맛있는 닭갈비다.");

        // ConstraintViolation은 유효성 검사에서 발견된 제약 조건 위반을 나타내는 클래스다.
        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[Bad] 유효성 검사에 실패하는 경우 (blank) ")
    void whenInvalidRequest_thenValidationErrorsBlank() {
        RecipeRequestDto dto = RecipeRequestDto.of("", "");

        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 유효성 검사에 실패하는 경우 (null) ")
    void whenInvalidRequest_thenValidationErrorsNull() {
        RecipeRequestDto dto = RecipeRequestDto.of(null, null);

        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Happy] 재료에 특수문자가 입력되어 들어오면 예외를 발생시킨다.")
    void notValidSpecialWordIngredient() {
        RecipeRequestDto dto = ingredientsSpecialWord("닭갈비", "젤 맛난 닭갈비", "####닭고기");

        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Happy] 재료는 [재료명,(공백)] 구조로 데이터가 들어와야만 한다. ex(재료1, 재료2, 재료3 )")
    void validBlankIngredient() {
        RecipeRequestDto dto = ingredientsSpecialWord("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마");

        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[bad] 재료 맨 마지막에 ,가 존재하면 예외가 발생한다.")
    void validBlankIngredient2() {
        RecipeRequestDto dto = ingredientsSpecialWord("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마,");

        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료 맨 마지막에 공백이 존재하면 예외가 발생한다.")
    void validBlankIngredient3() {
        RecipeRequestDto dto = ingredientsSpecialWord("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마 ");

        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[Happy] 해시태그에 특수문자가 입력되어 들어오면 예외를 발생시킨다.")
    void notValidSpecialWordHashtag() {

        RecipeRequestDto dto = hashTagSpecialWord("닭갈비", "젤 맛난 닭갈비", "밥###");

        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    private RecipeRequestDto ingredientsSpecialWord(String recipe, String recipeDesc, String ingredients) {
        return RecipeRequestDto.builder()
                .recipeName(recipe)
                .recipeDesc(recipeDesc)
                .ingredient(ingredients)
                .build();
    }

    private RecipeRequestDto hashTagSpecialWord(String recipe, String recipeDesc, String hashtag) {
        return RecipeRequestDto.builder()
                .recipeName(recipe)
                .recipeDesc(recipeDesc)
                .hashtag(hashtag)
                .build();
    }


}
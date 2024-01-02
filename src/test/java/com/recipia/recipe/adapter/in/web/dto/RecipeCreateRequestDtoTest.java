package com.recipia.recipe.adapter.in.web.dto;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[단위] 레시피 저장/업데이트에 사용되는 RequestDto 테스트")
class RecipeCreateRequestDtoTest {

    // Java의 Bean Validation API를 사용해 유효성 검증을 수행하기 위한 Validator 객체를 생성
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    @DisplayName("[happy] 입력값이 레시피명(작성) 레시피 설명(작성)이면 데이터를 받는다.")
    void validInputTest() {
        RecipeCreateRequestDto dto = RecipeCreateRequestDto.of("닭갈비", "세상에서 제일 맛있는 닭갈비다.");

        // ConstraintViolation은 유효성 검사에서 발견된 제약 조건 위반을 나타내는 클래스다.
        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[Bad] 입력값이 레시피명(공백) 레시피 설명(공백)이면 예외가 발생한다.(not blank)")
    void blankInputTest() {
        RecipeCreateRequestDto dto = RecipeCreateRequestDto.of("", "");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 입력값이 레시피명(작성) 레시피 설명(공백)이면 예외가 발생한다.(not blank)")
    void shouldFailWhenDescriptionIsBlank() {
        RecipeCreateRequestDto dto = RecipeCreateRequestDto.of("레시피명", "");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 입력값이 레시피명(공백) 레시피 설명(작성)이면 예외가 발생한다.(not blank)")
    void shouldFailWhenNameIsBlank() {
        RecipeCreateRequestDto dto = RecipeCreateRequestDto.of("", "레시피 설명");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 레시피명(null) 레시피 설명(null)이면 예외가 발생한다.(not null)")
    void nullInputTest() {
        RecipeCreateRequestDto dto = RecipeCreateRequestDto.of(null, null);

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 레시피명(작성) 레시피 설명(null)이면 예외가 발생한다.(not null)")
    void shouldFailWhenDescriptionIsNull() {
        RecipeCreateRequestDto dto = RecipeCreateRequestDto.of("레시피명", null);

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 레시피명(null) 레시피 설명(작성)이면 예외가 발생한다.(not null)")
    void shouldFailWhenNameIsNull() {
        RecipeCreateRequestDto dto = RecipeCreateRequestDto.of(null, "레시피 설명");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[happy] [재료명,(공백)] 구조로 재료 데이터가 들어오면 사용 가능하다. ex(재료1, 재료2, 재료3)")
    void multipleIngredients() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[happy] 재료는 한개만 입력해도 된다. [재료명,(공백)] 패턴이 한개의 재료일때는 뒷부분이 사라진다.")
    void singleIngredient() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[bad] 재료를 한개만 입력했지만 특수문자를 넣었다면 예외가 발생한다.")
    void ingredientWithSpecialChar() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기#");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료를 한개만 입력했지만 뒤에 공백을 넣었다면 오류가 발생한다.")
    void ingredientWithTrailingSpace() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기 ");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료를 한개만 입력했지만 앞에 공백을 넣었다면 오류가 발생한다.")
    void ingredientWithLeadingSpace() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", " 닭고기");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료가 [재료명,(공백)] 패턴을 어겼다면 예외가 발생한다.")
    void incorrectIngredientFormat() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기,  고구마   , 튀김");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료에 특수문자가 들어있다면 예외를 발생시킨다.")
    void ingredientWithSpecialCharAll() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "####닭고기");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[bad] 재료 맨 마지막에 ,가 존재하면 예외가 발생한다.")
    void ingredientWithTrailingComma() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마,");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료 맨 마지막에 공백이 존재하면 예외가 발생한다.")
    void ingredientWithTrailingSpaceAll() {
        RecipeCreateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마 ");

        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

//    @Test
//    @DisplayName("[bad] 해시태그에 특수문자가 입력되어 들어오면 예외를 발생시킨다.")
//    void notValidSpecialWordHashtag() {
//        RecipeRequestDto dto = hashTagSpecialWord("닭갈비", "젤 맛난 닭갈비", "밥###");
//
//        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);
//
//        assertThat(violations).isNotEmpty();
//        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
//    }

    @Test
    @DisplayName("[happy] 모든 유효성 검사를 통과하는 경우")
    void successfulValidation() {
        createRecipeForFullValidation("닭갈비", "세상에서 제일 맛있는 닭갈비다.", "닭고기", "닭갈비");
        RecipeCreateRequestDto dto = RecipeCreateRequestDto.of("닭갈비", "세상에서 제일 맛있는 닭갈비다.");

        // ConstraintViolation은 유효성 검사에서 발견된 제약 조건 위반을 나타내는 클래스다.
        Set<ConstraintViolation<RecipeCreateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    // 재료 테스트에서 사용
    private RecipeCreateRequestDto createRecipeWithIngredients(String recipe, String recipeDesc, String ingredients) {
        return RecipeCreateRequestDto.builder()
                .recipeName(recipe)
                .recipeDesc(recipeDesc)
                .ingredient(ingredients)
                .build();
    }

    // 해시태그 테스트에서 사용
    private RecipeCreateRequestDto createRecipeWithHashtag(String recipe, String recipeDesc, String hashtag) {
        return RecipeCreateRequestDto.builder()
                .recipeName(recipe)
                .recipeDesc(recipeDesc)
                .hashtag(hashtag)
                .build();
    }

    // 모든 유효성 검증을 위해 사용
    private RecipeCreateRequestDto createRecipeForFullValidation(String recipe, String recipeDesc, String ingredients, String hashtag) {
        return RecipeCreateRequestDto.builder()
                .recipeName(recipe)
                .recipeDesc(recipeDesc)
                .ingredient(ingredients)
                .hashtag(hashtag)
                .build();
    }


}
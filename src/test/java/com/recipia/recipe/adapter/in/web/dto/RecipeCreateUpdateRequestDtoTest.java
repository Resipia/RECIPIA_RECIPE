package com.recipia.recipe.adapter.in.web.dto;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateUpdateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.SubCategoryDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[단위] 레시피 저장/업데이트에 사용되는 RequestDto 테스트")
class RecipeCreateUpdateRequestDtoTest {

    // Java의 Bean Validation API를 사용해 유효성 검증을 수행하기 위한 Validator 객체를 생성
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    @DisplayName("[happy] 입력값이 레시피명(작성) 레시피 설명(작성)이면 데이터를 받는다.")
    void validInputTest() {
        RecipeCreateUpdateRequestDto dto = RecipeCreateUpdateRequestDto.of("닭갈비", "세상에서 제일 맛있는 닭갈비다.");

        // ConstraintViolation은 유효성 검사에서 발견된 제약 조건 위반을 나타내는 클래스다.
        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[Bad] 입력값이 레시피명(공백) 레시피 설명(공백)이면 예외가 발생한다.(not blank)")
    void blankInputTest() {
        RecipeCreateUpdateRequestDto dto = RecipeCreateUpdateRequestDto.of("", "");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 입력값이 레시피명(작성) 레시피 설명(공백)이면 예외가 발생한다.(not blank)")
    void shouldFailWhenDescriptionIsBlank() {
        RecipeCreateUpdateRequestDto dto = RecipeCreateUpdateRequestDto.of("레시피명", "");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 입력값이 레시피명(공백) 레시피 설명(작성)이면 예외가 발생한다.(not blank)")
    void shouldFailWhenNameIsBlank() {
        RecipeCreateUpdateRequestDto dto = RecipeCreateUpdateRequestDto.of("", "레시피 설명");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 레시피명(null) 레시피 설명(null)이면 예외가 발생한다.(not null)")
    void nullInputTest() {
        RecipeCreateUpdateRequestDto dto = RecipeCreateUpdateRequestDto.of(null, null);

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 레시피명(작성) 레시피 설명(null)이면 예외가 발생한다.(not null)")
    void shouldFailWhenDescriptionIsNull() {
        RecipeCreateUpdateRequestDto dto = RecipeCreateUpdateRequestDto.of("레시피명", null);

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[Bad] 레시피명(null) 레시피 설명(작성)이면 예외가 발생한다.(not null)")
    void shouldFailWhenNameIsNull() {
        RecipeCreateUpdateRequestDto dto = RecipeCreateUpdateRequestDto.of(null, "레시피 설명");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[happy] [재료명,(공백)] 구조로 재료 데이터가 들어오면 사용 가능하다. ex(재료1, 재료2, 재료3)")
    void multipleIngredients() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[happy] 재료는 한개만 입력해도 된다. [재료명,(공백)] 패턴이 한개의 재료일때는 뒷부분이 사라진다.")
    void singleIngredient() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[bad] 재료를 한개만 입력했지만 특수문자를 넣었다면 예외가 발생한다.")
    void ingredientWithSpecialChar() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기#");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료를 한개만 입력했지만 뒤에 공백을 넣었다면 오류가 발생한다.")
    void ingredientWithTrailingSpace() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기 ");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료를 한개만 입력했지만 앞에 공백을 넣었다면 오류가 발생한다.")
    void ingredientWithLeadingSpace() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", " 닭고기");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료가 [재료명,(공백)] 패턴을 어겼다면 예외가 발생한다.")
    void incorrectIngredientFormat() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기,  고구마   , 튀김");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료에 특수문자가 들어있다면 예외를 발생시킨다.")
    void ingredientWithSpecialCharAll() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "####닭고기");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[bad] 재료 맨 마지막에 ,가 존재하면 예외가 발생한다.")
    void ingredientWithTrailingComma() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마,");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료 맨 마지막에 공백이 존재하면 예외가 발생한다.")
    void ingredientWithTrailingSpaceAll() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithIngredients("닭갈비", "젤 맛난 닭갈비", "닭고기, 감자, 고구마 ");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[happy] [해시태그,(공백)] 구조로 데이터가 들어오면 사용 가능하다. ex(재료1, 재료2, 재료3)")
    void multipleHashtagStruct() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", "해시태그1, 해시태그2, 해시태그3");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[happy] 해시태그는 한개만 입력해도 된다.")
    void singleHashtags() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", "해시태그1");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("[bad] 해시태그를 한개만 입력했지만 특수문자를 넣었다면 예외가 발생한다.")
    void hashtagsWithSpecialChar() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", "해시태그1#");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료를 한개만 입력했지만 뒤에 공백을 넣었다면 오류가 발생한다.")
    void hashtagsWithTrailingSpace() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", "해시태그1 ");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료를 한개만 입력했지만 앞에 공백을 넣었다면 오류가 발생한다.")
    void hashtagsWithLeadingSpace() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", " 해시태그1");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료가 [재료명,(공백)] 패턴을 어겼다면 예외가 발생한다.")
    void incorrectHashtagsFormat() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", "해시태그1,  해시태그2   , 해시태그3");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료에 특수문자가 들어있다면 예외를 발생시킨다.")
    void hashtagsWithSpecialCharAll() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", "####해시태그1");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1); // 두 필드 모두 유효성 검사에 실패했으므로
    }

    @Test
    @DisplayName("[bad] 재료 맨 마지막에 ,가 존재하면 예외가 발생한다.")
    void hashtagsWithTrailingComma() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", "해시태그1, 해시태그2, 해시태그3,");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[bad] 재료 맨 마지막에 공백이 존재하면 예외가 발생한다.")
    void hashtagsWithTrailingSpaceAll() {
        RecipeCreateUpdateRequestDto dto = createRecipeWithHashtag("닭갈비", "젤 맛난 닭갈비", "해시태그1, 해시태그2, 해시태그3 ");

        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("[happy] 모든 유효성 검사를 통과하는 경우")
    void successfulValidation() {
        createRecipeForFullValidation("닭갈비", "세상에서 제일 맛있는 닭갈비다.", "닭고기", "닭갈비");
        RecipeCreateUpdateRequestDto dto = RecipeCreateUpdateRequestDto.of("닭갈비", "세상에서 제일 맛있는 닭갈비다.");

        // ConstraintViolation은 유효성 검사에서 발견된 제약 조건 위반을 나타내는 클래스다.
        Set<ConstraintViolation<RecipeCreateUpdateRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    // 재료 테스트에서 사용
    private RecipeCreateUpdateRequestDto createRecipeWithIngredients(String recipe, String recipeDesc, String ingredients) {
        return RecipeCreateUpdateRequestDto.builder()
                .recipeName(recipe)
                .recipeDesc(recipeDesc)
                .ingredient(ingredients)
                .subCategoryDtoList(Arrays.asList(SubCategoryDto.of(1L)))
                .build();
    }

    // 해시태그 테스트에서 사용
    private RecipeCreateUpdateRequestDto createRecipeWithHashtag(String recipe, String recipeDesc, String hashtag) {
        return RecipeCreateUpdateRequestDto.builder()
                .recipeName(recipe)
                .recipeDesc(recipeDesc)
                .hashtag(hashtag)
                .subCategoryDtoList(Arrays.asList(SubCategoryDto.of(1L)))
                .build();
    }

    // 모든 유효성 검증을 위해 사용
    private RecipeCreateUpdateRequestDto createRecipeForFullValidation(String recipe, String recipeDesc, String ingredients, String hashtag) {
        return RecipeCreateUpdateRequestDto.builder()
                .recipeName(recipe)
                .recipeDesc(recipeDesc)
                .ingredient(ingredients)
                .hashtag(hashtag)
                .subCategoryDtoList(Arrays.asList(SubCategoryDto.of(1L)))
                .build();
    }


}
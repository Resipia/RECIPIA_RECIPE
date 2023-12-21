package com.recipia.recipe.adapter.in.web.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeRequestDtoTest {

    // Java의 Bean Validation API를 사용해 유효성 검증을 수행하기 위한 Validator 객체를 생성
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    @DisplayName("Happy Path - 유효성 검사를 통과하는 경우")
    void whenValidRequest_thenNoValidationErrors() {
        RecipeRequestDto dto = RecipeRequestDto.of("닭갈비", "세상에서 제일 맛있는 닭갈비다.");

        // ConstraintViolation은 유효성 검사에서 발견된 제약 조건 위반을 나타내는 클래스다.
        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Bad Path - 유효성 검사에 실패하는 경우")
    void whenInvalidRequest_thenValidationErrors() {
        RecipeRequestDto dto = RecipeRequestDto.of("", "");

        Set<ConstraintViolation<RecipeRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2); // 두 필드 모두 유효성 검사에 실패했으므로
    }

}
package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("[단위] 댓글 삭제에 사용되는 request dto 테스트")
class CommentDeleteRequestDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("[happy] 필수 데이터가 전부 입력된 채 요청이 들어오면 데이터를 받는다.")
    @Test
    void validInputTest() {
        // given
        CommentDeleteRequestDto dto = CommentDeleteRequestDto.of(1L, 1L);
        // when
        Set<ConstraintViolation<CommentDeleteRequestDto>> violations = validator.validate(dto);
        // then
        assertThat(violations).isEmpty();
    }

}
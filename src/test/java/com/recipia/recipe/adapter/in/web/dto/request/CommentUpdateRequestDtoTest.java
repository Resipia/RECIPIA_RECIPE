package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[단위] 댓글 수정에 사용되는 RequestDto 테스트")
class CommentUpdateRequestDtoTest {

    // Java의 Bean Validation API를 사용해 유효성 검증을 수행하기 위한 Validator 객체를 생성
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("[happy] 필수 데이터가 전부 입력된채 요청이 들어오면 데이터를 받는다.")
    @Test
    void validInputTest() {
        // given
        CommentUpdateRequestDto dto = CommentUpdateRequestDto.of(1L, "update-value");
        // when
        // ConstraintViolation은 유효성 검사에서 발견된 제약 조건 위반을 나타내는 클래스다.
        Set<ConstraintViolation<CommentUpdateRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
    }


}
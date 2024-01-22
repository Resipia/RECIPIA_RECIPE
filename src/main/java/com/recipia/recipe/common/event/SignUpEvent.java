package com.recipia.recipe.common.event;

/**
 * 회원가입 스프링 이벤트
 */
public record SignUpEvent(
        Long memberId
) {
}

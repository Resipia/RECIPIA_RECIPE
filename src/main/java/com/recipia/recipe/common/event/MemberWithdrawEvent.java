package com.recipia.recipe.common.event;

/**
 * 회원 탈퇴 스프링 이벤트
 */
public record MemberWithdrawEvent(
        Long memberId
) {
}

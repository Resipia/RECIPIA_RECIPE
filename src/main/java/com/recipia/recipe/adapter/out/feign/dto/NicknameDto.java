package com.recipia.recipe.adapter.out.feign.dto;

/**
 * Feign 클라이언트로 멤버의 Id, Nickname을 주고받는 데이터 전달 객체
 */
public record NicknameDto(
        Long memberId,
        String nickname
) {

    public static NicknameDto of(Long memberId, String nickname) {
        return new NicknameDto(memberId, nickname);
    }

}

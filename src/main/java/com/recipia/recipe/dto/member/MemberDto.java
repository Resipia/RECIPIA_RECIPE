package com.recipia.recipe.dto.member;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MemberDto {

    private Long memberId;
    private String username;
    private String nickname;

    @Builder
    private MemberDto(Long memberId, String username, String nickname) {
        this.memberId = memberId;
        this.username = username;
        this.nickname = nickname;
    }

    public static MemberDto of(Long memberId, String username, String nickname) {
        return new MemberDto(memberId, username, nickname);
    }
}

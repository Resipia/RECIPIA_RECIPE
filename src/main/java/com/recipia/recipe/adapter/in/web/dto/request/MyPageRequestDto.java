package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 마이페이지 조회 요청 dto
 */
@Data
@NoArgsConstructor
public class MyPageRequestDto {

    @NotNull
    private Long targetMemberId;

    private MyPageRequestDto(Long targetMemberId) {
        this.targetMemberId = targetMemberId;
    }
    public static MyPageRequestDto of(Long targetMemberId) {
        return new MyPageRequestDto(targetMemberId);
    }
}

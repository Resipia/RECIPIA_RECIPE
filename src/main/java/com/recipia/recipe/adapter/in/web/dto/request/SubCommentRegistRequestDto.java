package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대댓글 등록 요청 dto
 */
@Data
@NoArgsConstructor
public class SubCommentRegistRequestDto {

    @NotNull
    private Long parentCommentId;
    @NotNull
    private String subCommentText;

    private SubCommentRegistRequestDto(Long parentCommentId, String subCommentText) {
        this.parentCommentId = parentCommentId;
        this.subCommentText = subCommentText;
    }

    public static SubCommentRegistRequestDto of(Long parentCommentId, String subCommentText) {
        return new SubCommentRegistRequestDto(parentCommentId, subCommentText);
    }
}

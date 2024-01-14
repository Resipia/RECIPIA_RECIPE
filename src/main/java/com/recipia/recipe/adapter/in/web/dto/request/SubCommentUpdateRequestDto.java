package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대댓글 수정 요청 dto
 */
@Data
@NoArgsConstructor
public class SubCommentUpdateRequestDto {

    @NotNull
    private Long id;
    @NotBlank
    private String subCommentText;

    @Builder
    private SubCommentUpdateRequestDto(Long id, String subCommentText) {
        this.id = id;
        this.subCommentText = subCommentText;
    }

    public static SubCommentUpdateRequestDto of(Long id, String subCommentText) {
        return new SubCommentUpdateRequestDto(id, subCommentText);
    }
}

package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대댓글 삭제 요청 dto
 */
@Data
@NoArgsConstructor
public class SubCommentDeleteRequestDto {

    @NotNull
    private Long id;
    @NotNull
    private Long parentCommentId;

    private SubCommentDeleteRequestDto(Long id, Long parentCommentId) {
        this.id = id;
        this.parentCommentId = parentCommentId;
    }

    public static SubCommentDeleteRequestDto of(Long id, Long parentCommentId) {
        return new SubCommentDeleteRequestDto(id, parentCommentId);
    }
}

package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 삭제 요청 dto
 */
@Data
@NoArgsConstructor
public class CommentDeleteRequestDto {

    @NotNull
    private Long id;
    @NotNull
    private Long recipeId;

    private CommentDeleteRequestDto(Long id, Long recipeId) {
        this.id = id;
        this.recipeId = recipeId;
    }

    public static CommentDeleteRequestDto of(Long id, Long recipeId) {
        return new CommentDeleteRequestDto(id, recipeId);
    }
}

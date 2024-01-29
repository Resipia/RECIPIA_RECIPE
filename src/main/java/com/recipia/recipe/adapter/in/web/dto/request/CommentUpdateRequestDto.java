package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 수정 요청 dto
 */
@Data
@NoArgsConstructor
public class CommentUpdateRequestDto {

    @NotNull
    private Long id;
    @NotNull
    private Long recipeId;
    @NotBlank
    private String commentText;

    private CommentUpdateRequestDto(Long id, Long recipeId, String commentText) {
        this.id = id;
        this.recipeId = recipeId;
        this.commentText = commentText;
    }

    public static CommentUpdateRequestDto of(Long id, Long recipeId, String commentText) {
        return new CommentUpdateRequestDto(id, recipeId, commentText);
    }

    public static CommentUpdateRequestDto of(Long id, String commentText) {
        return new CommentUpdateRequestDto(id, null, commentText);
    }
}

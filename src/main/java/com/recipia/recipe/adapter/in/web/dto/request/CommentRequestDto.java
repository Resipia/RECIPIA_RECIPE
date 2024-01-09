package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 등록/수정 dto
 */
@Data
@NoArgsConstructor
public class CommentRequestDto {

    private Long id;
    @NotNull
    private Long recipeId;
    @NotBlank
    private String commentText;

    private CommentRequestDto(Long id, Long recipeId, String commentText) {
        this.id = id;
        this.recipeId = recipeId;
        this.commentText = commentText;
    }

    public static CommentRequestDto of(Long id, Long recipeId, String commentText) {
        return new CommentRequestDto(id, recipeId, commentText);
    }

    public static CommentRequestDto of(Long recipeId, String commentText) {
        return new CommentRequestDto(null, recipeId, commentText);
    }
}

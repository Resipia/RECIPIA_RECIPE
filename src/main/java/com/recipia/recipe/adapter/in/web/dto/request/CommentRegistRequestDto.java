package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 등록 요청 dto
 */
@Data
@NoArgsConstructor
public class CommentRegistRequestDto {

    private Long id;
    @NotNull
    private Long recipeId;
    @NotBlank
    private String commentText;

    private CommentRegistRequestDto(Long id, Long recipeId, String commentText) {
        this.id = id;
        this.recipeId = recipeId;
        this.commentText = commentText;
    }

    public static CommentRegistRequestDto of(Long id, Long recipeId, String commentText) {
        return new CommentRegistRequestDto(id, recipeId, commentText);
    }

    public static CommentRegistRequestDto of(Long recipeId, String commentText) {
        return new CommentRegistRequestDto(null, recipeId, commentText);
    }
}

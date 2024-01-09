package com.recipia.recipe.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 도메인 객체
 */
@NoArgsConstructor
@Getter
public class Comment {

    private Long id;
    private Long recipeId;
    private Long memberId;
    private String commentText;
    private String delYn;

    private Comment(Long id, Long recipeId, Long memberId, String commentText, String delYn) {
        this.id = id;
        this.recipeId = recipeId;
        this.memberId = memberId;
        this.commentText = commentText;
        this.delYn = delYn;
    }

    public static Comment of(Long id, Long recipeId, Long memberId, String commentText, String delYn) {
        return new Comment(id, recipeId, memberId, commentText, delYn);
    }



}

package com.recipia.recipe.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크 도메인 객체
 */
@NoArgsConstructor
@Getter
public class Bookmark {

    private Long id;
    private Long recipeId;
    private Long memberId;

    @Builder
    private Bookmark(Long id, Long recipeId, Long memberId) {
        this.id = id;
        this.recipeId = recipeId;
        this.memberId = memberId;
    }

    public static Bookmark of(Long id, Long recipeId, Long memberId) {
        return new Bookmark(id, recipeId, memberId);
    }

    public static Bookmark of(Long recipeId, Long memberId) {
        return new Bookmark(null, recipeId, memberId);
    }

}

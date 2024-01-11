package com.recipia.recipe.domain;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 좋아요 도메인 객체
 */
@NoArgsConstructor
@Getter
@Setter
public class RecipeLike {

    private Long id;
    private Recipe recipe;
    private Long memberId;

    @Builder
    private RecipeLike(Long id, Recipe recipe, Long memberId) {
        this.id = id;
        this.recipe = recipe;
        this.memberId = memberId;
    }

    public static RecipeLike of(Long id, Recipe recipe, Long memberId) {
        return new RecipeLike(id, recipe, memberId);
    }

    public static RecipeLike of(Recipe recipe, Long memberId) {
        return new RecipeLike(null, recipe, memberId);
    }


}

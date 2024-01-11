package com.recipia.recipe.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 좋아요 dto 객체
 */
@Data
@NoArgsConstructor
public class RecipeLikeRequestDto {

    Long recipeLikeId;

    @NotNull
    Long recipeId;

    @NotNull
    Long memberId;

    @Builder
    private RecipeLikeRequestDto(Long recipeLikeId, Long recipeId, Long memberId) {
        this.recipeLikeId = recipeLikeId;
        this.recipeId = recipeId;
        this.memberId = memberId;
    }

    public static RecipeLikeRequestDto of(Long recipeLikeId, Long recipeId, Long memberId) {
        return new RecipeLikeRequestDto(recipeLikeId, recipeId, memberId);
    }

    public static RecipeLikeRequestDto of(Long recipeId, Long memberId) {
        return new RecipeLikeRequestDto(null, recipeId, memberId);
    }

}

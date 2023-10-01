package com.recipia.recipe.dto;

import com.recipia.recipe.domain.Recipe;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.recipia.recipe.domain.Recipe}
 */
public record RecipeDto(
        Long id,
        Long memberId,
        String recipeName,
        String recipeDesc,
        Integer timeTaken,
        String nickname,
        String delYn,
        LocalDateTime createDateTime,
        LocalDateTime updateDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static RecipeDto of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String nickname, String delYn, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        return new RecipeDto(id, memberId, recipeName, recipeDesc, timeTaken, nickname, delYn, createDateTime, updateDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static RecipeDto fromEntity(Recipe entity) {
        return of(
                entity.getId(),
                entity.getMemberId(),
                entity.getRecipeName(),
                entity.getRecipeDesc(),
                entity.getTimeTaken(),
                entity.getNickname(),
                entity.getDelYn(),
                entity.getCreateDateTime(),
                entity.getUpdateDateTime()
        );

    }
}
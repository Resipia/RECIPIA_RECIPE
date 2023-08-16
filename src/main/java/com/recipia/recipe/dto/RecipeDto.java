package com.recipia.recipe.dto;

import com.diningtalk.recipe.domain.Recipe;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.diningtalk.recipe.domain.Recipe}
 */
public record RecipeDto(
        Long id,
        Long userId,
        String recipeName,
        String recipeDesc,
        Integer timeTaken,
        String createUsername,
        String updateUsername,
        String createNickname,
        String delYn,
        LocalDateTime createDateTime,
        LocalDateTime updateDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static RecipeDto of(Long id, Long userId, String recipeName, String recipeDesc, Integer timeTaken, String createUsername, String updateUsername, String createNickname, String delYn, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        return new RecipeDto(id, userId, recipeName, recipeDesc, timeTaken, createUsername, updateUsername, createNickname, delYn, createDateTime, updateDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static RecipeDto fromEntity(Recipe entity) {
        return of(
                entity.getId(),
                entity.getUserId(),
                entity.getRecipeName(),
                entity.getRecipeDesc(),
                entity.getTimeTaken(),
                entity.getCreateUsername(),
                entity.getUpdateUsername(),
                entity.getCreateNickname(),
                entity.getDelYn(),
                entity.getCreateDateTime(),
                entity.getUpdateDateTime()
        );

    }
}
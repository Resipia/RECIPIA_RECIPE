package com.recipia.recipe.dto;

import com.recipia.recipe.hexagonal.adapter.out.persistence.entity.RecipeViewCntEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link RecipeViewCntEntity}
 */
public record RecipeViewCntDto(
        Long id,
        Long recipeViewCountValue,
        LocalDateTime createDateTime,
        LocalDateTime updateDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static RecipeViewCntDto of(Long id, Long recipeViewCountValue, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        return new RecipeViewCntDto(id, recipeViewCountValue, createDateTime, updateDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static RecipeViewCntDto fromEntity(RecipeViewCntEntity entity) {
        return of(
                entity.getId(),
                entity.getRecipeViewCountValue(),
                entity.getCreateDateTime(),
                entity.getUpdateDateTime()
        );
    }
}
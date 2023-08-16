package com.recipia.recipe.dto;

import com.recipia.recipe.domain.Ingredient;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.recipia.recipe.domain.Ingredient}
 */
public record IngredientDto(
        Long id,
        String ingredientName,
        String delYn,
        LocalDateTime createDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static IngredientDto of(Long id, String ingredientName, String delYn, LocalDateTime createDateTime) {
        return new IngredientDto(id, ingredientName, delYn, createDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static IngredientDto fromEntity(Ingredient entity) {
        return of(
                entity.getId(),
                entity.getIngredientName(),
                entity.getDelYn(),
                entity.getCreateDateTime()
        );
    }
}
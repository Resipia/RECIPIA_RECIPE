package com.recipia.recipe.dto;

import com.recipia.recipe.hexagonal.adapter.out.persistence.entity.CustomIngredientEntity;
import com.recipia.recipe.hexagonal.adapter.out.persistence.entity.IngredientEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link IngredientEntity}
 */
public record CustomIngredientDto(
        Long id,
        String ingredientName,
        LocalDateTime createDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static CustomIngredientDto of(Long id, String ingredientName, LocalDateTime createDateTime) {
        return new CustomIngredientDto(id, ingredientName, createDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static CustomIngredientDto fromEntity(CustomIngredientEntity entity) {
        return of(
                entity.getId(),
                entity.getIngredientName(),
                entity.getCreateDateTime()
        );
    }
}
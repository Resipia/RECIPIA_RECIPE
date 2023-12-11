package com.recipia.recipe.dto;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeCtgryMapEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link RecipeCtgryMapEntity}
 */
public record RecipeCtgryMapDto(
        Long id,
        LocalDateTime createDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static RecipeCtgryMapDto of(Long id, LocalDateTime createDateTime) {
        return new RecipeCtgryMapDto(id, createDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static RecipeCtgryMapDto fromEntity(RecipeCtgryMapEntity entity) {
        return of(entity.getId(), entity.getCreateDateTime());
    }
}
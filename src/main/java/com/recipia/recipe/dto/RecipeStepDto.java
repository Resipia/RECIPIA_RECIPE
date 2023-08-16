package com.recipia.recipe.dto;

import com.recipia.recipe.domain.RecipeStep;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.recipia.recipe.domain.RecipeStep}
 */
public record RecipeStepDto(
        Long id,
        Integer step,
        String stepDescription,
        String delYn,
        LocalDateTime createDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static RecipeStepDto of(Long id, Integer step, String stepDescription, String delYn, LocalDateTime createDateTime) {
        return new RecipeStepDto(id, step, stepDescription, delYn, createDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static RecipeStepDto fromEntity(RecipeStep entity) {
        return of(
                entity.getId(),
                entity.getStep(),
                entity.getStepDescription(),
                entity.getDelYn(),
                entity.getCreateDateTime()
        );
    }
}
package com.recipia.recipe.dto;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeHistLogEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link RecipeHistLogEntity}
 */
public record RecipeHistLogDto(
        Long id,
        Long memberId,
        LocalDateTime createDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static RecipeHistLogDto of(Long id, Long memberId, LocalDateTime createDateTime) {
        return new RecipeHistLogDto(id, memberId, createDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static RecipeHistLogDto fromEntity(RecipeHistLogEntity entity) {
        return of(
                entity.getId(),
                entity.getMemberId(),
                entity.getCreateDateTime()
        );
    }
}
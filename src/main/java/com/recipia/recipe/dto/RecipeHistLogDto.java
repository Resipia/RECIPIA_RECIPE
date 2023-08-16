package com.recipia.recipe.dto;

import com.recipia.recipe.domain.RecipeFile;
import com.recipia.recipe.domain.RecipeHistLog;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.recipia.recipe.domain.RecipeHistLog}
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
    public static RecipeHistLogDto fromEntity(RecipeHistLog entity) {
        return of(
                entity.getId(),
                entity.getMemberId(),
                entity.getCreateDateTime()
        );
    }
}
package com.recipia.recipe.dto;

import com.diningtalk.recipe.domain.RecipeFile;
import com.diningtalk.recipe.domain.RecipeHistLog;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.diningtalk.recipe.domain.RecipeHistLog}
 */
public record RecipeHistLogDto(
        Long id,
        Long userId,
        LocalDateTime createDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static RecipeHistLogDto of(Long id, Long userId, LocalDateTime createDateTime) {
        return new RecipeHistLogDto(id, userId, createDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static RecipeHistLogDto fromEntity(RecipeHistLog entity) {
        return of(
                entity.getId(),
                entity.getUserId(),
                entity.getCreateDateTime()
        );
    }
}
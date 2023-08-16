package com.recipia.recipe.dto;

import com.diningtalk.recipe.domain.StarRate;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.diningtalk.recipe.domain.StarRate}
 */
public record StarRateDto(
        Long id,
        Long userId,
        Double starRateValue,
        LocalDateTime createDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static StarRateDto of(Long id, Long userId, Double starRateValue, LocalDateTime createDateTime) {
        return new StarRateDto(id, userId, starRateValue, createDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static StarRateDto fromEntity(StarRate entity) {
        return of(
                entity.getId(),
                entity.getUserId(),
                entity.getStarRateValue(),
                entity.getCreateDateTime()
        );
    }
}
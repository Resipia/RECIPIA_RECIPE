package com.recipia.recipe.dto;

import com.recipia.recipe.hexagonal.adapter.out.persistence.entity.StarRateEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link StarRateEntity}
 */
public record StarRateDto(
        Long id,
        Long memberId,
        Double starRateValue,
        LocalDateTime createDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static StarRateDto of(Long id, Long memberId, Double starRateValue, LocalDateTime createDateTime) {
        return new StarRateDto(id, memberId, starRateValue, createDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static StarRateDto fromEntity(StarRateEntity entity) {
        return of(
                entity.getId(),
                entity.getMemberId(),
                entity.getStarRateValue(),
                entity.getCreateDateTime()
        );
    }
}
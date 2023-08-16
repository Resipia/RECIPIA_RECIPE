package com.recipia.recipe.dto;

import com.diningtalk.recipe.domain.Ctgry;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.diningtalk.recipe.domain.Ctgry}
 */
public record CtgryDto(
        Long id,
        Long uppCtgryId,
        String ctgryName,
        Integer levelNo,
        Integer sortNo,
        String delYn,
        LocalDateTime createDateTime,
        LocalDateTime updateDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static CtgryDto of(Long id, Long uppCtgryId, String ctgryName, Integer levelNo, Integer sortNo, String delYn, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        return new CtgryDto(id, uppCtgryId, ctgryName, levelNo, sortNo, delYn, createDateTime, updateDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public static CtgryDto fromEntity(Ctgry entity) {
        return of(
                entity.getId(),
                entity.getUppCtgryId(),
                entity.getCtgryName(),
                entity.getLevelNo(),
                entity.getSortNo(),
                entity.getDelYn(),
                entity.getCreateDateTime(),
                entity.getUpdateDateTime()
        );
    }
}
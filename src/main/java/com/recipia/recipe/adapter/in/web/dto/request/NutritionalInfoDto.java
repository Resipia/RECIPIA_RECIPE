package com.recipia.recipe.adapter.in.web.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 영양소 DTO
 * 모든 영양소는 g을 기준으로 한다.
 */
@Data
@NoArgsConstructor
public class NutritionalInfoDto {

    private Long id; // pk
    private Integer carbohydrates;  // 탄수화물 함량
    private Integer protein; // 단백질 함량
    private Integer fat; // 지방 함량
    private Integer vitamins; // 비타민 함량
    private Integer minerals; // 미네랄 함량

    @Builder
    private NutritionalInfoDto(Long id, Integer carbohydrates, Integer protein, Integer fat, Integer vitamins, Integer minerals) {
        this.id = id;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.vitamins = vitamins;
        this.minerals = minerals;
    }

    public static NutritionalInfoDto of(Long id, Integer carbohydrates, Integer protein, Integer fat, Integer vitamins, Integer minerals) {
        return new NutritionalInfoDto(id, carbohydrates, protein, fat, vitamins, minerals);
    }

    public static NutritionalInfoDto of(Integer carbohydrates, Integer protein, Integer fat, Integer vitamins, Integer minerals) {
        return new NutritionalInfoDto(null, carbohydrates, protein, fat, vitamins, minerals);
    }

}

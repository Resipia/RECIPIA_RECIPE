package com.recipia.recipe.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NutritionalInfo {

    private Long id; // pk
    private Integer carbohydrates;  // 탄수화물 함량
    private Integer protein; // 단백질 함량
    private Integer fat; // 지방 함량
    private Integer vitamins; // 비타민 함량
    private Integer minerals; // 미네랄 함량

    @Builder
    private NutritionalInfo(Long id, Integer carbohydrates, Integer protein, Integer fat, Integer vitamins, Integer minerals) {
        this.id = id;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.vitamins = vitamins;
        this.minerals = minerals;
    }

    public static NutritionalInfo of(Long id, Integer carbohydrates, Integer protein, Integer fat, Integer vitamins, Integer minerals) {
        return new NutritionalInfo(id, carbohydrates, protein, fat, vitamins, minerals);
    }

    public static NutritionalInfo of(Integer carbohydrates, Integer protein, Integer fat, Integer vitamins, Integer minerals) {
        return new NutritionalInfo(null, carbohydrates, protein, fat, vitamins, minerals);
    }

}

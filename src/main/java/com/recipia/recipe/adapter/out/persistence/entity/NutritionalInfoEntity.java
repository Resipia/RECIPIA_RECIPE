package com.recipia.recipe.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "nutritional_info")
public class NutritionalInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutritional_info_id", nullable = false)
    private Long id;

    @Column(name = "carbohydrates")
    private Double carbohydrates;  // 탄수화물 함량

    @Column(name = "protein")
    private Double protein; // 단백질 함량

    @Column(name = "fat")
    private Double fat; // 지방 함량

    @Column(name = "vitamins")
    private Double vitamins; // 비타민 함량

    @Column(name = "minerals")
    private Double minerals; // 미네랄 함량

    @OneToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipe; // 해당 영양소 정보가 연결된 레시피. RecipeEntity와 1대1 관계

    @Builder
    private NutritionalInfoEntity(Long id, Double carbohydrates, Double protein, Double fat, Double vitamins, Double minerals, RecipeEntity recipe) {
        this.id = id;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.vitamins = vitamins;
        this.minerals = minerals;
        this.recipe = recipe;
    }

    public static NutritionalInfoEntity of(Long id, Double carbohydrates, Double protein, Double fat, Double vitamins, Double minerals, RecipeEntity recipe) {
        return new NutritionalInfoEntity(id, carbohydrates, protein, fat, vitamins, minerals, recipe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NutritionalInfoEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}


package com.recipia.recipe.hexagonal.adapter.out.persistence.entity;

import com.recipia.recipe.hexagonal.adapter.out.persistence.entity.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeCtgryMapEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ctgry_map_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ctgry_id", nullable = false)
    private CtgryEntity ctgryEntity;

    private RecipeCtgryMapEntity(RecipeEntity recipeEntity, CtgryEntity ctgryEntity) {
        this.recipeEntity = recipeEntity;
        this.ctgryEntity = ctgryEntity;
    }

    public static RecipeCtgryMapEntity of(RecipeEntity recipeEntity, CtgryEntity ctgryEntity) {
        return new RecipeCtgryMapEntity(recipeEntity, ctgryEntity);
    }
}

package com.recipia.recipe.adapter.out.persistence.entity;

import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "recipe_category_map")
public class RecipeCategoryMapEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue
    @Column(name = "recipe_category_map_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategoryEntity subCategoryEntity;

    @Builder
    private RecipeCategoryMapEntity(Long id, RecipeEntity recipeEntity, SubCategoryEntity subCategoryEntity) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.subCategoryEntity = subCategoryEntity;
    }

    public static RecipeCategoryMapEntity of(Long id, RecipeEntity recipeEntity, SubCategoryEntity subCategoryEntity) {
        return new RecipeCategoryMapEntity(id, recipeEntity, subCategoryEntity);
    }

    public static RecipeCategoryMapEntity of(RecipeEntity recipeEntity, SubCategoryEntity subCategoryEntity) {
        return new RecipeCategoryMapEntity(null, recipeEntity, subCategoryEntity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeCategoryMapEntity that)) return false;
        return this.id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

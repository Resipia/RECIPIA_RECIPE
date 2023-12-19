package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class IngredientRecipeMapEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_recipe_map_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @JoinColumn(name = "ingredient_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private IngredientEntity ingredientEntity;

    @ToString.Exclude
    @JoinColumn(name = "custom_ingredient_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomIngredientEntity customIngredientEntity;

    @ToString.Exclude
    @JoinColumn(name = "recipe_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RecipeEntity recipeEntity;

    // 생성자 factory method
    @Builder
    private IngredientRecipeMapEntity(IngredientEntity ingredientEntity, CustomIngredientEntity customIngredientEntity, RecipeEntity recipeEntity) {
        this.ingredientEntity = ingredientEntity;
        this.customIngredientEntity = customIngredientEntity;
        this.recipeEntity = recipeEntity;
    }

    // static method 생성
    public static IngredientRecipeMapEntity of(IngredientEntity ingredientEntity, CustomIngredientEntity customIngredientEntity, RecipeEntity recipeEntity) {
        return new IngredientRecipeMapEntity(ingredientEntity, customIngredientEntity, recipeEntity);
    }


}

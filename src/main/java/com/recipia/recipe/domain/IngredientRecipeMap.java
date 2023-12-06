package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.CreateDateTime;
import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class IngredientRecipeMap extends CreateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_recipe_map_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @JoinColumn(name = "ingredient_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Ingredient ingredient;

    @ToString.Exclude
    @JoinColumn(name = "custom_ingredient_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomIngredient customIngredient;

    @ToString.Exclude
    @JoinColumn(name = "recipe_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RecipeEntity recipeEntity;

    // 생성자 factory method
    @Builder
    private IngredientRecipeMap(Ingredient ingredient, CustomIngredient customIngredient, RecipeEntity recipeEntity) {
        this.ingredient = ingredient;
        this.customIngredient = customIngredient;
        this.recipeEntity = recipeEntity;
    }

    // static method 생성
    public static IngredientRecipeMap of(Ingredient ingredient, CustomIngredient customIngredient, RecipeEntity recipeEntity) {
        return new IngredientRecipeMap(ingredient, customIngredient, recipeEntity);
    }


}

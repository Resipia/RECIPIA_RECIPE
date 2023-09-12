package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.CreateDateTime;
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
    private Recipe recipe;

    // 생성자 factory method
    @Builder
    private IngredientRecipeMap(Ingredient ingredient, CustomIngredient customIngredient, Recipe recipe) {
        this.ingredient = ingredient;
        this.customIngredient = customIngredient;
        this.recipe = recipe;
    }

    // static method 생성
    public static IngredientRecipeMap of(Ingredient ingredient, CustomIngredient customIngredient, Recipe recipe) {
        return new IngredientRecipeMap(ingredient, customIngredient, recipe);
    }


}

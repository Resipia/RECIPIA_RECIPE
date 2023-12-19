package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class IngredientEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    private Long id;

    @Column(name = "ingredient_nm", nullable = false)
    private String ingredientName;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    private IngredientEntity(String ingredientName, String delYn, RecipeEntity recipeEntity) {
        this.ingredientName = ingredientName;
        this.delYn = delYn;
        this.recipeEntity = recipeEntity;
    }

    public static IngredientEntity of(String ingredientName, String delYn, RecipeEntity recipeEntity) {
        return new IngredientEntity(ingredientName, delYn, recipeEntity);
    }
}

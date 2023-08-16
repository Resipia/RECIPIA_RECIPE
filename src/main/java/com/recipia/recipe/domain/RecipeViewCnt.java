package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.UpdateDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeViewCnt extends UpdateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_view_cnt_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "recipe_view_cnt_value", nullable = false)
    private Long recipeViewCountValue;

    private RecipeViewCnt(Recipe recipe, Long recipeViewCountValue) {
        this.recipe = recipe;
        this.recipeViewCountValue = recipeViewCountValue;
    }

    public static RecipeViewCnt of(Recipe recipe, Long recipeViewCountValue) {
        return new RecipeViewCnt(recipe, recipeViewCountValue);
    }
}

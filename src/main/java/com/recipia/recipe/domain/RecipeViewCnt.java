package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.UpdateDateTime;
import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
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
    private RecipeEntity recipeEntity;

    @Column(name = "recipe_view_cnt_value", nullable = false)
    private Long recipeViewCountValue;

    private RecipeViewCnt(RecipeEntity recipeEntity, Long recipeViewCountValue) {
        this.recipeEntity = recipeEntity;
        this.recipeViewCountValue = recipeViewCountValue;
    }

    public static RecipeViewCnt of(RecipeEntity recipeEntity, Long recipeViewCountValue) {
        return new RecipeViewCnt(recipeEntity, recipeViewCountValue);
    }
}

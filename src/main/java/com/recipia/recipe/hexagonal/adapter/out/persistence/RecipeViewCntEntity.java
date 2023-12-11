package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeViewCntEntity extends UpdateDateTimeForEntity {

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

    private RecipeViewCntEntity(RecipeEntity recipeEntity, Long recipeViewCountValue) {
        this.recipeEntity = recipeEntity;
        this.recipeViewCountValue = recipeViewCountValue;
    }

    public static RecipeViewCntEntity of(RecipeEntity recipeEntity, Long recipeViewCountValue) {
        return new RecipeViewCntEntity(recipeEntity, recipeViewCountValue);
    }
}

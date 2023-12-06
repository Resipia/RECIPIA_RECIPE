package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.CreateDateTime;
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
public class RecipeCtgryMap extends CreateDateTime {

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
    private Ctgry ctgry;

    private RecipeCtgryMap(RecipeEntity recipeEntity, Ctgry ctgry) {
        this.recipeEntity = recipeEntity;
        this.ctgry = ctgry;
    }

    public static RecipeCtgryMap of(RecipeEntity recipeEntity, Ctgry ctgry) {
        return new RecipeCtgryMap(recipeEntity, ctgry);
    }
}

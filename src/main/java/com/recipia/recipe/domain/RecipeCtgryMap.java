package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.CreateDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "RECIPE_CTGRY_MAP")
public class RecipeCtgryMap extends CreateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ctgry_map_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ctgry_id", nullable = false)
    private Ctgry ctgry;

    private RecipeCtgryMap(Recipe recipe, Ctgry ctgry) {
        this.recipe = recipe;
        this.ctgry = ctgry;
    }

    public static RecipeCtgryMap of(Recipe recipe, Ctgry ctgry) {
        return new RecipeCtgryMap(recipe, ctgry);
    }
}

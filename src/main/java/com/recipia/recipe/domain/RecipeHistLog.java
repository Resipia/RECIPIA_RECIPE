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
public class RecipeHistLog extends CreateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_hist_log_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    private RecipeHistLog(RecipeEntity recipeEntity, Long memberId) {
        this.recipeEntity = recipeEntity;
        this.memberId = memberId;
    }

    public static RecipeHistLog of(RecipeEntity recipeEntity, Long memberId) {
        return new RecipeHistLog(recipeEntity, memberId);
    }
}

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
public class RecipeHistLogEntity extends CreateDateTimeForEntity {

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

    private RecipeHistLogEntity(RecipeEntity recipeEntity, Long memberId) {
        this.recipeEntity = recipeEntity;
        this.memberId = memberId;
    }

    public static RecipeHistLogEntity of(RecipeEntity recipeEntity, Long memberId) {
        return new RecipeHistLogEntity(recipeEntity, memberId);
    }
}

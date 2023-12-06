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
public class StarRate extends CreateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "star_rate_id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "star_rate_value", nullable = false)
    private Double starRateValue;

    private StarRate(Long memberId, RecipeEntity recipeEntity, Double starRateValue) {
        this.memberId = memberId;
        this.recipeEntity = recipeEntity;
        this.starRateValue = starRateValue;
    }

    public static StarRate of(Long memberId, RecipeEntity recipeEntity, Double starRateValue) {
        return new StarRate(memberId, recipeEntity, starRateValue);
    }

}

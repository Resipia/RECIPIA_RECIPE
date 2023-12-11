package com.recipia.recipe.hexagonal.adapter.out.persistence.entity;


import com.recipia.recipe.hexagonal.adapter.out.persistence.entity.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class StarRateEntity extends CreateDateTimeForEntity {

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

    private StarRateEntity(Long memberId, RecipeEntity recipeEntity, Double starRateValue) {
        this.memberId = memberId;
        this.recipeEntity = recipeEntity;
        this.starRateValue = starRateValue;
    }

    public static StarRateEntity of(Long memberId, RecipeEntity recipeEntity, Double starRateValue) {
        return new StarRateEntity(memberId, recipeEntity, starRateValue);
    }

}

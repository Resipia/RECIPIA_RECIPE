package com.recipia.recipe.domain;


import com.diningtalk.recipe.domain.auditingfield.CreateDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "STAR_RATE")
public class StarRate extends CreateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "star_rate_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "star_rate_value", nullable = false)
    private Double starRateValue;

    private StarRate(Long userId, Recipe recipe, Double starRateValue) {
        this.userId = userId;
        this.recipe = recipe;
        this.starRateValue = starRateValue;
    }

    public static StarRate of(Long userId, Recipe recipe, Double starRateValue) {
        return new StarRate(userId, recipe, starRateValue);
    }

}

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
@Table(name = "RECIPE_HIST_LOG")
public class RecipeHistLog extends CreateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_hist_log_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    private RecipeHistLog(Recipe recipe, Long memberId) {
        this.recipe = recipe;
        this.memberId = memberId;
    }

    public static RecipeHistLog of(Recipe recipe, Long memberId) {
        return new RecipeHistLog(recipe, memberId);
    }
}

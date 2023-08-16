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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private RecipeHistLog(Recipe recipe, Long userId) {
        this.recipe = recipe;
        this.userId = userId;
    }

    public static RecipeHistLog of(Recipe recipe, Long userId) {
        return new RecipeHistLog(recipe, userId);
    }
}

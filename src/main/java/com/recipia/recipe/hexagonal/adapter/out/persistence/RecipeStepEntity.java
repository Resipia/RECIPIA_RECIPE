package com.recipia.recipe.hexagonal.adapter.out.persistence;


import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeStepEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_step_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "step", nullable = false)
    private Integer step;

    @Column(name = "step_desc", nullable = false)
    private String stepDescription;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @ToString.Exclude
    @OneToMany(mappedBy = "recipeStepEntity")
    private List<RecipeFileEntity> recipeFileEntityList = new ArrayList<>();

    private RecipeStepEntity(RecipeEntity recipeEntity, Integer step, String stepDescription, String delYn) {
        this.recipeEntity = recipeEntity;
        this.step = step;
        this.stepDescription = stepDescription;
        this.delYn = delYn;
    }

    public static RecipeStepEntity of(RecipeEntity recipeEntity, Integer step, String stepDescription, String delYn) {
        return new RecipeStepEntity(recipeEntity, step, stepDescription, delYn);
    }
}

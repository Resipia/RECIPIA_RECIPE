package com.recipia.recipe.adapter.out.persistence.entity;

import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "recipe_view_cnt")
public class RecipeViewCntEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_view_cnt_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "recipe_view_cnt_value", nullable = false)
    private Long recipeViewCntValue;

    @Builder
    private RecipeViewCntEntity(Long id, RecipeEntity recipeEntity, Long recipeViewCntValue) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.recipeViewCntValue = recipeViewCntValue;
    }

    public static RecipeViewCntEntity of(Long id, RecipeEntity recipeEntity, Long recipeViewCntValue) {
        return new RecipeViewCntEntity(id, recipeEntity, recipeViewCntValue);
    }

    public static RecipeViewCntEntity of(RecipeEntity recipeEntity, Long recipeViewCntValue) {
        return new RecipeViewCntEntity(null, recipeEntity, recipeViewCntValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeViewCntEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}

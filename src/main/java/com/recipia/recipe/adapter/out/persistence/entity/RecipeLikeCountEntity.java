package com.recipia.recipe.adapter.out.persistence.entity;

import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "recipe_like_count")
public class RecipeLikeCountEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_like_count_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "like_count")
    private Integer likeCount;

    @Builder
    private RecipeLikeCountEntity(Long id, RecipeEntity recipeEntity, Integer likeCount) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.likeCount = likeCount;
    }

    public static RecipeLikeCountEntity of(Long id, RecipeEntity recipeEntity, Integer likeCount) {
        return new RecipeLikeCountEntity(id, recipeEntity, likeCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeLikeCountEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}

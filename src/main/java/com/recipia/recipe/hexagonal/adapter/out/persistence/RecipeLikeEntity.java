package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeLikeEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_like_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Builder
    private RecipeLikeEntity(Long id, RecipeEntity recipeEntity, Long memberId) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.memberId = memberId;
    }

    public static RecipeLikeEntity of(Long id, RecipeEntity recipeEntity, Long memberId) {
        return new RecipeLikeEntity(id, recipeEntity, memberId);
    }

    public static RecipeLikeEntity of(RecipeEntity recipeEntity, Long memberId) {
        return new RecipeLikeEntity(null, recipeEntity, memberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeLikeEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}

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
    private Integer viewCount;

    @Builder
    private RecipeViewCntEntity(Long id, RecipeEntity recipeEntity, Integer viewCount) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.viewCount = viewCount;
    }

    public static RecipeViewCntEntity of(Long id, RecipeEntity recipeEntity, Integer viewCount) {
        return new RecipeViewCntEntity(id, recipeEntity, viewCount);
    }

    public static RecipeViewCntEntity of(RecipeEntity recipeEntity, Integer viewCount) {
        return new RecipeViewCntEntity(null, recipeEntity, viewCount);
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

    /**
     * 조회수 변경감지에 사용
     */
    public void changeViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}

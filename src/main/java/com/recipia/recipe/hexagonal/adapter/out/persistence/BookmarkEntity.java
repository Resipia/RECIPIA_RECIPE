package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BookmarkEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipe;


    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Builder
    private BookmarkEntity(Long id, RecipeEntity recipe, Long memberId) {
        this.id = id;
        this.recipe = recipe;
        this.memberId = memberId;
    }

    // id를 가진 생성자 factory method
    public static BookmarkEntity of(Long id, RecipeEntity recipe, Long memberId) {
        return new BookmarkEntity(id, recipe, memberId);
    }

    // id가 null인 생성자 factory method
    public static BookmarkEntity of(RecipeEntity recipe, Long memberId) {
        return new BookmarkEntity(null, recipe, memberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookmarkEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

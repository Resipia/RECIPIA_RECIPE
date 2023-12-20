package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommentEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @Builder
    private CommentEntity(Long id, RecipeEntity recipe, Long memberId, String commentText, String delYn) {
        this.id = id;
        this.recipeEntity = recipe;
        this.memberId = memberId;
        this.commentText = commentText;
        this.delYn = delYn;
    }

    public static CommentEntity of(Long id, RecipeEntity recipe, Long memberId, String commentText, String delYn) {
        return new CommentEntity(id, recipe, memberId, commentText, delYn);
    }

    public static CommentEntity of(RecipeEntity recipe, Long memberId, String commentText, String delYn) {
        return new CommentEntity(null, recipe, memberId, commentText, delYn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentEntity commentEntity)) return false;
        return this.id != null && Objects.equals(getId(), commentEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}

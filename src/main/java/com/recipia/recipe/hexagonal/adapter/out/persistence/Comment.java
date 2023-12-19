package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipe;

    @JoinColumn(name = "member_id", nullable = false)
    private Long memberId;

    @JoinColumn(name = "comment_text", nullable = false)
    private String commentText;

    @JoinColumn(name = "del_yn", nullable = false)
    private String delYn;

    @Builder
    private Comment(Long id, RecipeEntity recipe, Long memberId, String commentText, String delYn) {
        this.id = id;
        this.recipe = recipe;
        this.memberId = memberId;
        this.commentText = commentText;
        this.delYn = delYn;
    }

    public static Comment of(Long id, RecipeEntity recipe, Long memberId, String commentText, String delYn) {
        return new Comment(id, recipe, memberId, commentText, delYn);
    }

    public static Comment of(RecipeEntity recipe, Long memberId, String commentText, String delYn) {
        return new Comment(null, recipe, memberId, commentText, delYn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return this.id != null && Objects.equals(getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}

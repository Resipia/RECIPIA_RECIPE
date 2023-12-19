package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SubCommentEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcomment_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity commentEntity;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "subcomment_text", nullable = false)
    private String subcommentText;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @Builder
    private SubCommentEntity(Long id, CommentEntity commentEntity, Long memberId, String subcommentText, String delYn) {
        this.id = id;
        this.commentEntity = commentEntity;
        this.memberId = memberId;
        this.subcommentText = subcommentText;
        this.delYn = delYn;
    }

    public static SubCommentEntity of(Long id, CommentEntity commentEntity, Long memberId, String subcommentText, String delYn) {
        return new SubCommentEntity(id, commentEntity, memberId, subcommentText, delYn);
    }

    public static SubCommentEntity of(CommentEntity commentEntity, Long memberId, String subcommentText, String delYn) {
        return new SubCommentEntity(null, commentEntity, memberId, subcommentText, delYn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubCommentEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}

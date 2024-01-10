package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.recipia.recipe.adapter.out.persistence.entity.QCommentEntity.commentEntity;

@RequiredArgsConstructor
@Repository
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * [UPDATE] commentId에 해당하는 댓글 내용을 수정한다.
     */
    public Long updateComment(Comment comment) {
        return queryFactory
                .update(commentEntity)
                .set(commentEntity.commentText, comment.getCommentText())
                .where(commentEntity.id.eq(comment.getId()))
                .execute();
    }

    /**
     * [DELETE] commentId에 해당하는 댓글을 삭제처리(del_yn = 'Y')한다.
     */
    public Long softDeleteComment(Long commentId) {
        return queryFactory
                .update(commentEntity)
                .set(commentEntity.delYn, "Y")
                .where(commentEntity.id.eq(commentId))
                .execute();
    }
}

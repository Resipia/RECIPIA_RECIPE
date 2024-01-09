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

    public Long updateComment(Comment comment) {
        return queryFactory
                .update(commentEntity)
                .set(commentEntity.commentText, comment.getCommentText())
                .where(commentEntity.id.eq(comment.getId()), commentEntity.delYn.eq(comment.getDelYn()))
                .execute();
    }

}

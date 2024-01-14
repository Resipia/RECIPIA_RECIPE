package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.domain.SubComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.recipia.recipe.adapter.out.persistence.entity.QSubCommentEntity.subCommentEntity;

@RequiredArgsConstructor
@Repository
public class SubCommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * [UPDATE] subCommentId에 해당하는 대댓글 내용을 수정한다.
     */
    public Long updateSubComment(SubComment subComment) {
        return queryFactory
                .update(subCommentEntity)
                .set(subCommentEntity.subcommentText, subComment.getSubCommentText())
                .where(subCommentEntity.id.eq(subComment.getId()))
                .execute();
    }
}

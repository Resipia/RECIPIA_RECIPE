package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.recipia.recipe.adapter.out.persistence.entity.QCommentEntity.commentEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeEntity.recipeEntity;

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

    /**
     * recipeId에 해당하는 (삭제되지 않은) 댓글을 sortType으로 정렬해 Comment Entity 리스트를 반환한다.
     */
    public Page<CommentEntity> getCommentEntityList(Long recipeId, Pageable pageable, String sortType) {
        // 기본 쿼리 설정
        JPAQuery<CommentEntity> query = queryFactory
                .selectFrom(commentEntity)
                .where(commentEntity.recipeEntity.id.eq(recipeId), commentEntity.delYn.eq("N"));

        // 정렬 조건 적용
        query = switch (sortType) {
            case "new" -> query.orderBy(commentEntity.createDateTime.desc());
            case "old" -> query.orderBy(commentEntity.createDateTime.asc());
            default -> query.orderBy(recipeEntity.createDateTime.desc()); // 기본 정렬 조건
        };

        // sort 적용 이후 메인 쿼리 실행
        List<CommentEntity> entityList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 (결과가 null일 경우 0으로 세팅해 NPE 방지)
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(commentEntity.count())
                        .from(commentEntity)
                        .where(commentEntity.recipeEntity.id.eq(recipeId), commentEntity.delYn.eq("N"))
                        .fetchOne())
                .orElse(0L);


        return new PageImpl<>(entityList, pageable, totalCount);
    }
}

package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.recipia.recipe.adapter.out.persistence.entity.QCommentEntity.commentEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QNicknameEntity.nicknameEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QSubCommentEntity.subCommentEntity;

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
                .set(commentEntity.updateDateTime, LocalDateTime.now())
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
                .set(commentEntity.updateDateTime, LocalDateTime.now())
                .where(commentEntity.id.eq(commentId))
                .execute();
    }

    /**
     * [READ] recipeId에 해당하는 (삭제되지 않은) 댓글을 sortType으로 정렬해 CommentListResponseDto 리스트를 반환한다.
     */
    public Page<CommentListResponseDto> getCommentDtoList(Long recipeId, Pageable pageable, String sortType) {

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(commentEntity.memberId));

        // 댓글에 작성된 대댓글 갯수 조회 서브쿼리
        JPQLQuery<Long> subCommentCountSubQuery = JPAExpressions
                .select(subCommentEntity.count())
                .from(subCommentEntity)
                .where(subCommentEntity.commentEntity.id.eq(commentEntity.id), subCommentEntity.delYn.eq("N"));

        // 기본 쿼리 설정
        JPAQuery<CommentListResponseDto> query = queryFactory
                .select(Projections.constructor(
                        CommentListResponseDto.class,
                        commentEntity.id,
                        commentEntity.memberId,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        commentEntity.commentText,
                        Expressions.stringTemplate("TO_CHAR({0}, 'YYYY-MM-DD')", commentEntity.createDateTime),
//                        commentEntity.createDateTime,
                        commentEntity.createDateTime.ne(commentEntity.updateDateTime),
                        ExpressionUtils.as(subCommentCountSubQuery, "subCommentCount")
                ))
                .from(commentEntity)
                .where(commentEntity.recipeEntity.id.eq(recipeId), commentEntity.delYn.eq("N"));


        // 정렬 조건 적용
        query = applySort(query, sortType);


        // 쿼리 실행 및 결과 페이징
        List<CommentListResponseDto> dtoList = query
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


        return new PageImpl<>(dtoList, pageable, totalCount);
    }

    private JPAQuery<CommentListResponseDto> applySort(JPAQuery<CommentListResponseDto> query, String sortType) {
        return switch (sortType) {
            case "new" -> query.orderBy(commentEntity.createDateTime.desc());
            case "old" -> query.orderBy(commentEntity.createDateTime.asc());
            default -> query.orderBy(commentEntity.createDateTime.desc());
        };
    }

    /**
     * [READ] recipeId에 해당하는 Comment entity의 id값을 List로 반환한다.
     */
    public List<Long> findCommentIdsByRecipeId(Long recipeId) {
        return queryFactory
                .select(commentEntity.id)
                .from(commentEntity)
                .where(commentEntity.recipeEntity.id.eq(recipeId))
                .fetch();
    }

    /**
     * [DELETE] recipeId에 해당하는 댓글을 삭제처리 (del_yn = Y)한다.
     */
    public Long softDeleteCommentByRecipeId(Long recipeId) {
        return queryFactory
                .update(commentEntity)
                .set(commentEntity.delYn, "Y")
                .set(commentEntity.updateDateTime, LocalDateTime.now())
                .where(commentEntity.recipeEntity.id.eq(recipeId))
                .execute();
    }
}

package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.adapter.in.web.dto.response.SubCommentListResponseDto;
import com.recipia.recipe.domain.SubComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.recipia.recipe.adapter.out.persistence.entity.QNicknameEntity.nicknameEntity;
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

    /**
     * [DELETE] subCommentId에 해당하는 댓글을 삭제처리(del_yn = 'Y')한다.
     */
    public Long softDeleteSubComment(Long subCommentId) {
        return queryFactory
                .update(subCommentEntity)
                .set(subCommentEntity.delYn, "Y")
                .where(subCommentEntity.id.eq(subCommentId))
                .execute();
    }

    /**
     * [READ] parentCommentId에 해당하는 (삭제되지 않은) 대댓글을 등록순으로 정렬해 SubCommentListResponseDto 리스트를 반환한다.
     */
    public Page<SubCommentListResponseDto> getSubCommentDtoList(Long parentCommentId, Pageable pageable) {

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(subCommentEntity.memberId));

        // 기본 쿼리 설정
        JPAQuery<SubCommentListResponseDto> query = queryFactory
                .select(Projections.constructor(
                        SubCommentListResponseDto.class,
                        subCommentEntity.id,
                        subCommentEntity.commentEntity.id,
                        subCommentEntity.memberId,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        subCommentEntity.subcommentText,
                        Expressions.stringTemplate("TO_CHAR({0}, 'YYYY-MM-DD')", subCommentEntity.createDateTime),
                        subCommentEntity.createDateTime.ne(subCommentEntity.updateDateTime)
                ))
                .from(subCommentEntity)
                .where(subCommentEntity.commentEntity.id.eq(parentCommentId), subCommentEntity.delYn.eq("N"))
                .orderBy(subCommentEntity.createDateTime.asc());    // 기본으로 오래된순으로 정렬

        // 쿼리 실행 및 결과 페이징
        List<SubCommentListResponseDto> dtoList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 (결과가 null일 경우 0으로 세팅해 NPE 방지)
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(subCommentEntity.count())
                        .from(subCommentEntity)
                        .where(subCommentEntity.commentEntity.id.eq(parentCommentId), subCommentEntity.delYn.eq("N"))
                        .fetchOne())
                .orElse(0L);


        return new PageImpl<>(dtoList, pageable, totalCount);
    }

    /**
     * [DELETE] commentId에 해당하는 대댓글을 삭제처리(del_yn = "Y")한다.
     */
    public Long softDeleteSubCommentByCommentIds(List<Long> commentIds) {
        return queryFactory
                .update(subCommentEntity)
                .set(subCommentEntity.delYn, "Y")
                .where(subCommentEntity.commentEntity.id.in(commentIds))
                .execute();
    }
}

package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.SubCommentListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistence.entity.SubCommentEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.CommentQueryRepository;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.SubCommentQueryRepository;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.SubComment;
import com.recipia.recipe.domain.converter.CommentConverter;
import com.recipia.recipe.domain.converter.SubCommentConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentAdapter implements CommentPort {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final CommentQueryRepository commentQueryRepository;
    private final SubCommentRepository subCommentRepository;
    private final SubCommentConverter subCommentConverter;
    private final SubCommentQueryRepository subCommentQueryRepository;

    /**
     * [CREATE] 댓글 저장
     * 저장에 성공하면 생성된 댓글의 pk값을 반환한다.
     */
    @Override
    public Long createComment(Comment comment) {
        CommentEntity commentEntity = commentConverter.domainToEntity(comment);
        return commentRepository.save(commentEntity).getId();
    }

    /**
     * [UPDATE] 댓글 수정
     * 댓글 수정에 성공하면 업데이트 된 row의 갯수를 반환한다.
     */
    @Override
    public Long updateComment(Comment comment) {
        return commentQueryRepository.updateComment(comment);
    }

    /**
     * [READ] commentId, memberId, del_yn으로 댓글 검색
     * 조건에 해당하는 댓글을 검색해서 댓글이 존재하면 true, 존재하지 않으면 false를 반환한다.
     */
    @Override
    public boolean checkIsCommentExistAndMine(Comment comment) {
        Optional<CommentEntity> commentEntity = commentRepository.findByIdAndMemberIdAndDelYn(comment.getId(), comment.getMemberId(), "N");
        return commentEntity.isPresent();
    }

    /**
     * [DELETE] 댓글 삭제
     * 댓글 삭제(del_yn = "Y"로 컬럼 update)에 성공하면 업데이트 된 row의 갯수를 반환한다.
     */
    @Override
    public Long softDeleteComment(Comment comment) {
        return commentQueryRepository.softDeleteComment(comment.getId());
    }

    /**
     * [READ] recipeId에 해당하는 댓글 목록 조회
     * querydsl을 사용해서 댓글 목록 조회 최적화 (목록, count)
     */
    @Override
    public Page<CommentListResponseDto> getCommentList(Long recipeId, Pageable pageable, String sortType) {
        return commentQueryRepository.getCommentDtoList(recipeId, pageable, sortType);
    }

    /**
     * [READ] commentId, del_yn으로 댓글 검색
     * 조건에 해당하는 댓글을 검색해서 댓글이 존재하면 true, 존재하지 않으면 false를 반환한다.
     */
    @Override
    public boolean checkIsCommentExist(Long parentCommentId) {
        Optional<CommentEntity> commentEntity = commentRepository.findByIdAndDelYn(parentCommentId, "N");
        return commentEntity.isPresent();
    }

    /**
     * [CREATE] 대댓글 저장
     * 저장에 성공하면 생성된 대댓글의 pk값을 반환한다.
     */
    @Override
    public Long createSubComment(SubComment subComment) {
        SubCommentEntity entity = subCommentConverter.domainToEntity(subComment);
        return subCommentRepository.save(entity).getId();
    }


    /**
     * [READ] subCommentId, memberId, del_yn으로 대댓글 검색
     * 조건에 해당하는 대댓글을 검색해서 대댓글이 존재하면 true, 존재하지 않으면 false를 반환한다.
     */
    @Override
    public boolean checkIsSubCommentExistAndMine(SubComment subComment) {
        Optional<SubCommentEntity> subCommentEntity = subCommentRepository.findByIdAndMemberIdAndDelYn(subComment.getId(), subComment.getMemberId(), "N");
        return subCommentEntity.isPresent();
    }

    /**
     * [UPDATE] 대댓글 수정
     * 대댓글 수정에 성공하면 업데이트 된 row의 갯수를 반환한다.
     */
    @Override
    public Long updateSubComment(SubComment subComment) {
        return subCommentQueryRepository.updateSubComment(subComment);
    }

    /**
     * [DELETE] 대댓글 삭제
     * 대댓글 삭제(del_yn = "Y"로 컬럼 update)에 성공하면 업데이트 된 row의 갯수를 반환한다.
     */
    @Override
    public Long softDeleteSubComment(SubComment subComment) {
        return subCommentQueryRepository.softDeleteSubComment(subComment.getId());
    }

    /**
     * [READ] parentCommentId 해당하는 대댓글 목록 조회
     * querydsl을 사용해서 대댓글 목록 조회 최적화 (목록, count)
     */
    @Override
    public Page<SubCommentListResponseDto> getSubCommentList(Long parentCommentId, Pageable pageable) {
        return subCommentQueryRepository.getSubCommentDtoList(parentCommentId, pageable);
    }

    /**
     * recipeId에 해당하는 댓글/대댓글 삭제처리(soft delete)
     */
    @Override
    public void softDeleteAllCommentsWithSubComments(Long recipeId) {
        // recipeId에 해당하는 commentId를 가져온다.
        List<Long> commentIds = commentQueryRepository.findCommentIdsByRecipeId(recipeId);
        // 그리고 댓글을 삭제처리한다.
        commentQueryRepository.softDeleteCommentByRecipeId(recipeId);
        // commentId에 해당하는 대댓글을 전부 삭제처리한다.
        subCommentQueryRepository.softDeleteSubCommentByCommentIds(commentIds);
    }

}

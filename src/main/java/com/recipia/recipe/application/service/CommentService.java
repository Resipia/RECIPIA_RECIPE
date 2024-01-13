package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.application.port.in.CommentUseCase;
import com.recipia.recipe.application.port.in.SubCommentUseCase;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.SubComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글/대댓글 서비스 클래스
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService implements CommentUseCase, SubCommentUseCase {

    private final CommentPort commentPort;
    private final RecipePort recipePort;

    /**
     * [CREATE] 댓글 등록을 담당하는 메서드
     * 1단계 - 댓글을 등록하려는 레시피가 존재하는지 검증한다.
     * 2단계 - 레시피가 존재한다면 댓글을 등록한다.
     */
    @Transactional
    @Override
    public Long createComment(Comment comment) {
        // 1단계 - 삭제되지 않은 레시피인지 검증
        checkIsRecipeExist(comment);
        return commentPort.createComment(comment);
    }

    /**
     * [UPDATE] 댓글 수정을 담당하는 메서드
     * 1단계 - 댓글을 달려는 레시피가 삭제되지 않은 레시피인지 검증한다. (recipeId, delYn으로 검색)
     * 2단계 - 수정을 요청한 댓글이 삭제되지 않은 댓글인지, 그리고 내가 원작자인지 검증한다. (commentId, memberId, delYn으로 검색)
     * 3단계 - 위 단계를 전부 패스했다면 댓글 내용을 수정한다.
     */
    @Transactional
    @Override
    public Long updateComment(Comment comment) {
        // 1단계 - 삭제되지 않은 레시피인지 검증
        checkIsRecipeExist(comment);

        // 2단계 - 삭제된 댓글이 아니고 내가 작성한 댓글이 맞는지 검증
        checkIsCommentExistAndMine(comment);

        // 3단계 - 댓글 수정
        return commentPort.updateComment(comment);
    }



    /**
     * [DELETE] 댓글 삭제를 담당하는 메서드
     * 1단계 - 댓글을 삭제하려는 레시피가 삭제되지 않은 레시피인지 검증한다. (recipeId, delYn으로 검색)
     * 2단계 - 삭제 요청한 댓글이 삭제되지 않은 댓글인지, 그리고 내가 원작자인지 검증한다. (commentId, memberId, delYn으로 검색)
     * 3단계 - 위 단계를 전부 패스했다면 댓글을 삭제한다.
     */
    @Transactional
    @Override
    public Long softDeleteComment(Comment comment) {
        // 1단계 - 삭제되지 않은 레시피인지 검증
        checkIsRecipeExist(comment);

        // 2단계 - 삭제된 댓글이 아니고 내가 작성한 댓글이 맞는지 검증
        checkIsCommentExistAndMine(comment);

        // 3단계 - 댓글 삭제
        return commentPort.softDeleteComment(comment);
    }

    /**
     * [READ] recipeId에 해당하는 댓글 목록 조회
     * 페이징을 위한 Pageable 객체를 여기서 조립해서 사용한다.
     * page=0과 size=10으로 Pageable 객체를 생성하면, 이는 '첫 번째 페이지에 10개의 항목을 보여달라'는 요청이다.
     * page=1과 size=10이면 '두 번째 페이지에 10개의 항목을 보여달라'는 요청이다.
     */
    @Override
    public PagingResponseDto<CommentListResponseDto> getCommentList(Long recipeId, int page, int size, String sortType) {
        // 1. 정렬조건을 정한 뒤 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 2. 데이터를 받아온다.
        Page<CommentListResponseDto> commentList = commentPort.getCommentList(recipeId, pageable, sortType);

        // 3. 받아온 데이터를 꺼내서 응답 dto에 값을 세팅해준다.
        List<CommentListResponseDto> content = commentList.getContent();
        Long totalCount = commentList.getTotalElements();
        return PagingResponseDto.of(content, totalCount);
    }

    /**
     * [CREATE] 대댓글 등록을 담당하는 메서드
     * 1단계 - 대댓글을 등록하려는 댓글이 존재하는지 검증한다.
     * 2단계 - 상위 댓글이 존재한다면 대댓글을 등록한다.
     */
    @Override
    public Long createSubComment(SubComment subComment) {
        // 1단계 - 삭제되지 않은 댓글인지 검증
        checkIsCommentExist(subComment.getParentCommentId());
        return commentPort.createSubComment(subComment);
    }

    /**
     * [READ] 레시피가 존재하는지 확인하는 메서드
     * recipeId로 레시피가 존재하는지, 삭제되었는지 검증하는 내부 메서드
     */
    public boolean checkIsRecipeExist(Comment comment) {
        Recipe recipe = Recipe.of(comment.getRecipeId());
        boolean isRecipeExist = recipePort.checkIsRecipeExist(recipe);
        if(!isRecipeExist) {
            throw new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND);
        }
        return true;
    }

    /**
     * [READ] 삭제된 댓글이 아니고 내가 작성한 댓글이 맞는지 검증
     * commentId로 댓글이 존재하고, memberId로 내가 원작자인지 검증하는 내부 메서드
     */
    public boolean checkIsCommentExistAndMine(Comment comment) {
        boolean isCommentExistAndMine = commentPort.checkIsCommentExistAndMine(comment);
        if(!isCommentExistAndMine) {
            throw new RecipeApplicationException(ErrorCode.COMMENT_IS_NOT_MINE);
        }
        return true;
    }


    /**
     * [READ] 삭제된 댓글이 아닌지 검증
     * commentId로 댓글이 존재하는지 검증하는 내부 메서드
     */
    public boolean checkIsCommentExist(Long parentCommentId) {
        boolean isCommentExist = commentPort.checkIsCommentExist(parentCommentId);
        if(!isCommentExist) {
            throw new RecipeApplicationException(ErrorCode.COMMENT_NOT_FOUND);
        }
        return true;
    }


}

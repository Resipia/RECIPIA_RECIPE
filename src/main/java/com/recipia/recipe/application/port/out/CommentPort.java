package com.recipia.recipe.application.port.out;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.SubCommentListResponseDto;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.SubComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentPort {
    Long createComment(Comment comment);

    Long updateComment(Comment comment);

    boolean checkIsCommentExistAndMine(Comment comment);

    Long softDeleteComment(Comment comment);

    Page<CommentListResponseDto> getCommentList(Long recipeId, Pageable pageable, String sortType);

    boolean checkIsCommentExist(Long parentCommentId);

    Long createSubComment(SubComment subComment);

    boolean checkIsSubCommentExistAndMine(SubComment subComment);

    Long updateSubComment(SubComment subComment);

    Long softDeleteSubComment(SubComment subComment);

    Page<SubCommentListResponseDto> getSubCommentList(Long parentCommentId, Pageable pageable);

    void softDeleteCommentsAndSubCommentsInRecipeIds(List<Long> recipeIds);

    Long softDeleteCommentsAndSubCommentsInMemberId(Long memberId);
}

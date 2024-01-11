package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.domain.Comment;

public interface CommentUseCase {
    Long createComment(Comment comment);
    Long updateComment(Comment comment);
    Long softDeleteComment(Comment comment);
    PagingResponseDto<CommentListResponseDto> getCommentList(Long recipeId, int page, int size, String sortType);
}

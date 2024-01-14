package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.SubCommentListResponseDto;
import com.recipia.recipe.domain.SubComment;

public interface SubCommentUseCase {
    Long createSubComment(SubComment subComment);
    Long updateSubComment(SubComment subComment);
    Long deleteSubComment(SubComment subComment);
    PagingResponseDto<SubCommentListResponseDto> getSubCommentList(Long parentCommentId, int page, int size);

}

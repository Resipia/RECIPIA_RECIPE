package com.recipia.recipe.application.port.out;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentPort {
    Long createComment(Comment comment);

    Long updateComment(Comment comment);

    boolean checkIsCommentExistAndMine(Comment comment);

    Long softDeleteComment(Comment comment);

    Page<CommentListResponseDto> getCommentList(Long recipeId, Pageable pageable, String sortType);
}

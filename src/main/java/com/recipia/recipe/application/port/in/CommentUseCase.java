package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Comment;

public interface CommentUseCase {
    Long createComment(Comment comment);
}

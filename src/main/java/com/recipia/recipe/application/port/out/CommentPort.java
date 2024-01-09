package com.recipia.recipe.application.port.out;

import com.recipia.recipe.domain.Comment;

public interface CommentPort {
    Long createComment(Comment comment);
}

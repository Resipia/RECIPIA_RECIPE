package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.CommentUseCase;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService implements CommentUseCase {

    private final CommentPort commentPort;

    @Override
    public Long createComment(Comment comment) {
        return commentPort.createComment(comment);
    }

}

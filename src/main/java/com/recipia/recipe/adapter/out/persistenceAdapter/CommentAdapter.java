package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.converter.CommentConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentAdapter implements CommentPort {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;

    @Override
    public Long createComment(Comment comment) {
        CommentEntity commentEntity = commentConverter.domainToEntity(comment);
        return commentRepository.save(commentEntity).getId();
    }

}

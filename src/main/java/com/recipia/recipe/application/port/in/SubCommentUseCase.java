package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.SubComment;

public interface SubCommentUseCase {
    Long createSubComment(SubComment subComment);
    Long updateSubComment(SubComment subComment);
    Long deleteSubComment(SubComment subComment);
}

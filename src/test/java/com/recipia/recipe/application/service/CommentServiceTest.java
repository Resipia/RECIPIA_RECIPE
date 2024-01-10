package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.Recipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위] 댓글 서비스 테스트")
class CommentServiceTest {

    @InjectMocks
    private CommentService sut;
    @Mock
    private CommentPort commentPort;
    @Mock
    private RecipePort recipePort;

    @DisplayName("[happy] recipeId, memberId, commentText가 정상적으로 들어오면 댓글 저장에 성공한다.")
    @Test
    void saveCommentSuccess() {
        // given
        Comment comment = Comment.of(null, 1L, 1L, "comment", "N");
        when(recipePort.checkIsRecipeExist(any(Recipe.class))).thenReturn(true);
        when(commentPort.createComment(comment)).thenReturn(1L);
        when(sut.createComment(comment)).thenReturn(1L);
        // when
        Long savedCommentId = sut.createComment(comment);
        // then
        assertEquals(savedCommentId, 1L);

    }

    @DisplayName("[happy] commentId, memberId, commentText가 정상적으로 들어오면 댓글 수정에 성공한다.")
    @Test
    void updateCommentSuccess() {
        // given
        Comment comment = Comment.of(1L, null, 1L, "update-comment", "N");
        when(recipePort.checkIsRecipeExist(any(Recipe.class))).thenReturn(true);
        when(commentPort.checkIsCommentExistAndMine(comment)).thenReturn(true);
        when(commentPort.updateComment(comment)).thenReturn(1L);
        // when
        Long updatedCount = sut.updateComment(comment);
        // then
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }

    @DisplayName("[happy] commentId, memberId가 정상적으로 들어오면 댓글 삭제에 성공한다.")
    @Test
    void softDeleteCommentSuccess() {
        // given
        Comment comment = Comment.of(1L, null, 1L, "delete-comment", "N");
        when(recipePort.checkIsRecipeExist(any(Recipe.class))).thenReturn(true);
        when(commentPort.checkIsCommentExistAndMine(comment)).thenReturn(true);
        when(commentPort.softDeleteComment(comment)).thenReturn(1L);

        // when
        Long deletedCount = sut.softDeleteComment(comment);

        // then
        assertThat(deletedCount).isEqualTo(1L);
    }

}
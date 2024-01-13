package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.SubComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위] 댓글/대댓글 서비스 테스트")
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

    @DisplayName("[happy] recipeId와 기본 페이징으로 댓글 목록을 정상적으로 가져온다.")
    @Test
    void whenGetCommentList_thenReturnsPagedComments() {
        // given
        Long recipeId = 1L;
        int page = 0;
        int size = 10;
        String sortType = "new";

        // 테스트에서 예상되는 반환 값. (여기서는 하나의 댓글 데이터만 포함)
        List<CommentListResponseDto> mockCommentList = List.of(
                CommentListResponseDto.of(1L, 1L, "nickname", "commentValue", "2022-11-22", false)
        );

        // PageImpl을 사용해 mockPage 객체 생성
        Page<CommentListResponseDto> mockPage = new PageImpl<>(mockCommentList);

        // mock 설정
        when(commentPort.getCommentList(eq(recipeId), any(Pageable.class), eq(sortType)))
                .thenReturn(mockPage);

        // when
        PagingResponseDto<CommentListResponseDto> result = sut.getCommentList(recipeId, page, size, sortType);

        // then
        assertThat(result.getContent()).hasSize(mockCommentList.size());
        assertThat(result.getContent()).containsExactlyElementsOf(mockCommentList);
    }

    @DisplayName("[happy] parentCommentId, memberId, subCommentText가 정상적으로 들어오면 대댓글 저장에 성공한다.")
    @Test
    void saveSubCommentSuccess() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, "subValue", "N");
        when(commentPort.checkIsCommentExist(subComment.getParentCommentId())).thenReturn(true);
        when(commentPort.createSubComment(subComment)).thenReturn(1L);
        when(sut.createSubComment(subComment)).thenReturn(1L);


        // when
        Long savedSubCommentId = sut.createSubComment(subComment);
        // then
        assertEquals(savedSubCommentId, 1L);

    }

}
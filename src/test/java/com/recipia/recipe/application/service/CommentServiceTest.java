package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.SubCommentListResponseDto;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @DisplayName("[happy] subCommentId, subCommentText가 정상적으로 들어오면 대댓글 수정에 성공한다.")
    @Test
    void updateSubCommentSuccess() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, "update-subcomment", "N");
        when(commentPort.checkIsCommentExist(subComment.getParentCommentId())).thenReturn(true);
        when(commentPort.checkIsSubCommentExistAndMine(subComment)).thenReturn(true);
        when(commentPort.updateSubComment(subComment)).thenReturn(1L);
        // when
        Long updatedCount = sut.updateSubComment(subComment);
        // then
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
        assertEquals(updatedCount, 1L);
    }

    @DisplayName("[happy] DB에 존재하는 recipeId로 요청받으면 true를 반환한다.")
    @Test
    void checkIsRecipeExistSuccess() {
        // given
        Comment comment = Comment.of(1L, 1L, 1L, "comment", "N");
        when(recipePort.checkIsRecipeExist(any(Recipe.class))).thenReturn(true);
        // when
        boolean isRecipeExist = sut.checkIsRecipeExist(comment);
        // then
        assertTrue(isRecipeExist);
    }

    @DisplayName("[bad] DB에 존재하지 않는 recipeId로 요청받으면 에러를 발생시킨다.")
    @Test
    void checkIsRecipeExistFail() {
        // given
        Comment comment = Comment.of(1L, 999L, 1L, "comment", "N");
        when(recipePort.checkIsRecipeExist(any(Recipe.class))).thenReturn(false);

        //when & then
        assertThatThrownBy(() -> sut.checkIsRecipeExist(comment))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    @DisplayName("[happy] commentId, memberId로 검색했을때 삭제되지 않은 댓글이고 내가 작성한 댓글일때 true를 반환한다.")
    @Test
    void checkIsCommentExistAndMineSuccess() {
        // given
        Comment comment = Comment.of(1L, 1L, 1L, "comment", "N");
        when(commentPort.checkIsCommentExistAndMine(comment)).thenReturn(true);
        // when
        boolean isCommentExistAndMine = sut.checkIsCommentExistAndMine(comment);
        // then
        assertTrue(isCommentExistAndMine);
    }

    @DisplayName("[bad] commentId, memberId로 검색했을때 데이터가 없으면 에러를 발생시킨다.")
    @Test
    void checkIsCommentExistAndMineFail() {
        // given
        Comment comment = Comment.of(1L, 999L, 1L, "comment", "N");
        when(commentPort.checkIsCommentExistAndMine(comment)).thenReturn(false);

        //when & then
        assertThatThrownBy(() -> sut.checkIsCommentExistAndMine(comment))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("요청자가 작성한 댓글이 아닙니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_IS_NOT_MINE);
    }

    @DisplayName("[happy] commentId로 검색했을때 삭제되지 않은 댓글일때 true를 반환한다.")
    @Test
    void checkIsCommentExistSuccess() {
        // given
        Long parentComment = 1L;
        when(commentPort.checkIsCommentExist(parentComment)).thenReturn(true);
        // when
        boolean isCommentExist = sut.checkIsCommentExist(parentComment);
        // then
        assertTrue(isCommentExist);
    }

    @DisplayName("[bad] commentId로 검색했을때 데이터가 없으면 에러를 발생시킨다.")
    @Test
    void checkIsCommentExistFail() {
        // given
        Long parentComment = 999L;
        when(commentPort.checkIsCommentExist(parentComment)).thenReturn(false);

        //when & then
        assertThatThrownBy(() -> sut.checkIsCommentExist(parentComment))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("댓글이 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);
    }

    @DisplayName("[happy] subCommentId, memberId로 검색했을때 삭제되지 않은 대댓글에 내가 작성한 대댓글일때 true를 반환한다.")
    @Test
    void checkIsSubCommentExistAndMineSuccess() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, 1L, "sub", "N");
        when(commentPort.checkIsSubCommentExistAndMine(subComment)).thenReturn(true);
        // when
        boolean isSubCommentExistAndMine = sut.checkIsSubCommentExistAndMine(subComment);
        // then
        assertTrue(isSubCommentExistAndMine);
    }

    @DisplayName("[bad] subCommentId, memberId로 검색했을때 데이터가 없다면 에러를 반환한다.")
    @Test
    void checkIsSubCommentExistAndMineFail() {
        // given
        SubComment subComment = SubComment.of(999L, 1L, 1L, "sub", "N");
        when(commentPort.checkIsSubCommentExistAndMine(subComment)).thenReturn(false);

        //when & then
        assertThatThrownBy(() -> sut.checkIsSubCommentExistAndMine(subComment))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("요청자가 작성한 대댓글이 아닙니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SUB_COMMENT_IS_NOT_MINE);
    }

    @DisplayName("[happy] subCommentId, subCommentText가 정상적으로 들어오면 대댓글 수정에 성공한다.")
    @Test
    void deleteSubCommentSuccess() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, 1L, "Y");
        when(commentPort.checkIsCommentExist(subComment.getParentCommentId())).thenReturn(true);
        when(commentPort.checkIsSubCommentExistAndMine(subComment)).thenReturn(true);
        when(commentPort.softDeleteSubComment(subComment)).thenReturn(1L);
        // when
        Long updatedCount = sut.deleteSubComment(subComment);
        // then
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
        assertEquals(updatedCount, 1L);
    }

    @DisplayName("[happy] parentCommentId와 기본 페이징으로 댓글 목록을 정상적으로 가져온다.")
    @Test
    void whenGetSubCommentList_thenReturnsPagedComments() {
        // given
        Long parentCommentId = 1L;
        int page = 0;
        int size = 10;

        // 테스트에서 예상되는 반환 값. (여기서는 두개의 대댓글 데이터만 포함)
        List<SubCommentListResponseDto> mockSubCommentList = List.of(
                SubCommentListResponseDto.of(1L, 1L, 1L, "member-1-nickname", "first-subComment", "2024-01-14", false),
                SubCommentListResponseDto.of(2L, 1L, 2L, "member-2-nickname", "second-subComment", "2024-01-14", false)
        );

        // PageImpl을 사용해 mockPage 객체 생성
        Page<SubCommentListResponseDto> mockPage = new PageImpl<>(mockSubCommentList);

        // mock 설정
        when(commentPort.getSubCommentList(eq(parentCommentId), any(Pageable.class)))
                .thenReturn(mockPage);

        // when
        PagingResponseDto<SubCommentListResponseDto> result = sut.getSubCommentList(parentCommentId, page, size);

        // then
        assertThat(result.getContent()).hasSize(mockSubCommentList.size());
        assertThat(result.getContent()).containsExactlyElementsOf(mockSubCommentList);
    }



}
package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.CommentRepository;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@DisplayName("[통합] 댓글 queryDsl 테스트")
class CommentQueryRepositoryTest extends TotalTestSupport {

    @Autowired
    private CommentQueryRepository sut;
    @Autowired
    private CommentRepository commentRepository;    // 실제 데이터베이스와 상호작용 확인용

    @DisplayName("[happy] 댓글이 성공적으로 업데이트 된다")
    @Test
    void updateCommentSuccess() {
        // given
        Comment comment = Comment.of(1L, null, 1L, "update-new-comment", "N");
        // when
        sut.updateComment(comment);
        // then
        CommentEntity updatedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(updatedComment.getCommentText()).isEqualTo(comment.getCommentText());
        assertThat(updatedComment.getCreateDateTime()).isNotEqualTo(updatedComment.getUpdateDateTime());
    }

    @DisplayName("[happy] 댓글이 성공적으로 삭제처리(del_yn = 'Y') 된다.")
    @Test
    void softDeleteCommentSuccess() {
        // given
        Long commentId = 1L;
        // when
        sut.softDeleteComment(commentId);
        // then
        CommentEntity updatedComment = commentRepository.findById(commentId).orElseThrow();
        assertThat(updatedComment.getDelYn()).isEqualTo("Y");
    }

    @DisplayName("[happy] recipeId에 해당하는 댓글 목록을 페이징하여 조회한다.")
    @Test
    void getAllCommentListSuccess() {
        // given
        Long recipeId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";

        // when
        Page<CommentListResponseDto> result = sut.getCommentDtoList(recipeId, pageable, sortType);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull();
        result.getContent().forEach(commentDto -> {
            assertThat(commentDto.getId()).isNotNull();
            assertThat(commentDto.getCommentValue()).isNotNull();
            assertThat(commentDto.getMemberId()).isNotNull();
            assertThat(commentDto.getNickname()).isNotNull();
        });
    }

    @DisplayName("[happy] recipeId에 해당하는 Comment pk값인 id를 List 형태로 반환한다.")
    @Test
    void findCommentIdsByRecipeId() {
        // given
        Long recipeId = 1L;
        // when
        List<Long> commentIdsByRecipeId = sut.findCommentIdsByRecipeId(recipeId);
        // then
        assertThat(commentIdsByRecipeId.size()).isEqualTo(1);
    }

    @DisplayName("[happy] recipeId에 해당하는 댓글이 존재할때 soft delete 한다.")
    @Test
    void softDeleteCommentByRecipeId() {
        // given
        Long recipeId = 1L;
        // when
        sut.softDeleteCommentByRecipeId(recipeId);
        // then
        List<CommentEntity> allByRecipeEntityId = commentRepository.findAllByRecipeEntity_Id(recipeId);
        assertThat(allByRecipeEntityId.get(0).getDelYn()).isEqualTo("Y");
    }

    @DisplayName("[happy] 댓글 목록을 조회할때 댓글에 포함된 대댓글의 갯수도 함께 반환한다.")
    @Test
    void getCommentDtoListWithSubCommentCount() {
        // given
        Long recipeId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";

        // when
        Page<CommentListResponseDto> commentDtoList = sut.getCommentDtoList(recipeId, pageable, sortType);

        // then
        List<CommentListResponseDto> commentList = commentDtoList.getContent();
        assertThat(commentList.get(0).getSubCommentCount()).isEqualTo(2L);
    }
}
package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistence.entity.SubCommentEntity;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.SubComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("[통합] 댓글 Adapter 테스트")
class CommentAdapterTest extends TotalTestSupport {

    @Autowired
    private CommentAdapter commentAdapter;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SubCommentRepository subCommentRepository;

    @DisplayName("[happy] 댓글 저장에 성공하면 저장된 comment id값을 반환한다.")
    @Test
    void createCommentSuccess() {
        // given
        Comment comment = Comment.of(null, 1L, 1L, "comment", "N");
        // when
        Long createCommentId = commentAdapter.createComment(comment);

        // then
        CommentEntity savedCommentEntity = commentRepository.findById(createCommentId).get();
        assertThat(createCommentId).isNotNull();
        assertThat(createCommentId).isEqualTo(savedCommentEntity.getId());
    }

    @DisplayName("[happy] 댓글 수정에 성공하면 수정된 데이터 갯수를 반환한다.")
    @Test
    void updateCommentSuccess() {
        // given
        Comment comment = Comment.of(1L, null, 1L, "update-comment", "N");
        // when
        Long updatedCount = commentAdapter.updateComment(comment);

        // then
        String commentText = commentRepository.findById(comment.getId()).get().getCommentText();
        assertThat(commentText).isEqualTo(comment.getCommentText());
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }

    @DisplayName("[happy] commentId, memberId, delYn에 해당하는 댓글이 존재할때 true를 반환한다.")
    @Test
    void whenMyCommentExistReturnTrue() {
        // given
        Comment comment = Comment.of(1L, null, 1L, "update-comment", "N");
        // when
        boolean isCommentExist = commentAdapter.checkIsCommentExistAndMine(comment);
        // then
        assertTrue(isCommentExist);
    }

    @DisplayName("[happy] 댓글을 soft delete 처리(del_yn = 'Y')해주고 업데이트된 row의 갯수를 반환한다.")
    @Test
    void deleteCommentSuccess() {
        // given
        Comment comment = Comment.of(1L, 1L, 1L, null, "N");
        // when
        Long updatedCount = commentAdapter.softDeleteComment(comment);
        // then
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }

    @DisplayName("[happy] 유효한 recipeId와 페이지 정보와 정렬 유형이 주어지면, 페이징 처리된 댓글 목록이 반환된다.")
    @Test
    void getRecipeCommentListSuccess() {
        // given
        Long recipeId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";
        // when
        Page<CommentListResponseDto> result = commentAdapter.getCommentList(recipeId, pageable, sortType);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull();
        assertThat(result.getTotalElements()).isGreaterThan(0);

        // 결과의 일부를 검증 (첫 번째 댓글의 특정 필드)
        CommentListResponseDto firstComment = result.getContent().get(0);
        assertThat(firstComment.getId()).isNotNull();
        assertThat(firstComment.getNickname()).isNotNull();
        assertThat(firstComment.getCommentValue()).isNotEmpty();

        // createDate 형식 확인
        String expectedDateFormat = "\\d{4}-\\d{2}-\\d{2}"; // yyyy-MM-dd 형식 정규식
        assertThat(firstComment.getCreateDate()).matches(expectedDateFormat);

        // isUpdated 필드 검증
        // 이 부분은 실제 데이터에 따라 달라질 수 있음
        assertThat(firstComment.isUpdated()).isFalse();
    }

    @DisplayName("[happy] commentId, delYn에 해당하는 댓글이 존재할때 true를 반환한다.")
    @Test
    void whenCommentExistReturnTrue() {
        // given
        Long parentCommentId = 1L;
        // when
        boolean isCommentExist = commentAdapter.checkIsCommentExist(parentCommentId);
        // then
        assertTrue(isCommentExist);
    }


    @DisplayName("[happy] 대댓글 저장에 성공하면 저장된 subComment id값을 반환한다.")
    @Test
    void createSubCommentSuccess() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, "subValue", "N");
        // when
        Long createdSubCommentId = commentAdapter.createSubComment(subComment);

        // then
        SubCommentEntity savedSubCommentId = subCommentRepository.findById(createdSubCommentId).get();
        assertThat(createdSubCommentId).isNotNull();
        assertThat(createdSubCommentId).isEqualTo(savedSubCommentId.getId());
    }

    @DisplayName("[happy] subCommentId, memberId, del_yn에 해당하는 대댓글이 존재할때 true를 반환한다.")
    @Test
    void whenMySubCommentExistReturnTrue() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, 1L, "sub-value", "N");
        // when
        boolean isMySubCommentExist = commentAdapter.checkIsSubCommentExistAndMine(subComment);
        // then
        assertTrue(isMySubCommentExist);
    }

    @DisplayName("[happy] 대댓글 수정에 성공하면 수정된 데이터 갯수를 반환한다.")
    @Test
    void updateSubCommentSuccess() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, 1L, "update-sub-value", "N");
        // when
        Long updatedCount = commentAdapter.updateSubComment(subComment);
        // then
        String subCommentText = subCommentRepository.findById(subComment.getId()).get().getSubcommentText();
        assertThat(subCommentText).isEqualTo(subComment.getSubCommentText());
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }


    @DisplayName("[happy] 대댓글을 soft delete 처리(del_yn = 'Y')해주고 업데이트된 row의 갯수를 반환한다.")
    @Test
    void deleteSubCommentSuccess() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, 1L, "N");
        // when
        Long updatedCount = commentAdapter.softDeleteSubComment(subComment);
        // then
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }

}
package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("[통합] 댓글 Adapter 테스트")
class CommentAdapterTest extends TotalTestSupport {

    @Autowired
    private CommentAdapter commentAdapter;
    @Autowired
    private CommentRepository commentRepository;

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
    void whenCommentExistReturnTrue() {
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

}
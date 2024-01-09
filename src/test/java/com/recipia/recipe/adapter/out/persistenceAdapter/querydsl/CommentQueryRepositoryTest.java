package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.CommentRepository;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    }
}
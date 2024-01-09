package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.converter.CommentConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        Comment comment = Comment.of(1L, 1L, "comment", "N");
        // when
        Long createCommentId = commentAdapter.createComment(comment);

        // then
        CommentEntity savedCommentEntity = commentRepository.findById(createCommentId).get();
        assertThat(createCommentId).isNotNull();
        assertThat(createCommentId).isEqualTo(savedCommentEntity.getId());
    }

}
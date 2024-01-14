package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.out.persistence.entity.SubCommentEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.SubCommentRepository;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.SubComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@DisplayName("[통합] 대댓글 queryDsl 테스트")
class SubCommentQueryRepositoryTest extends TotalTestSupport {

    @Autowired
    private SubCommentQueryRepository sut;
    @Autowired
    private SubCommentRepository subCommentRepository;

    @DisplayName("[happy] 대댓글이 성공적으로 업데이트 된다.")
    @Test
    void updateSubCommentSuccess() {
        // given
        SubComment subComment = SubComment.of(1L, 1L, 1L, "hello", "N");
        // when
        sut.updateSubComment(subComment);
        // then
        SubCommentEntity updatedEntity = subCommentRepository.findById(1L).get();
        assertEquals(updatedEntity.getSubcommentText(), subComment.getSubCommentText());
    }

}
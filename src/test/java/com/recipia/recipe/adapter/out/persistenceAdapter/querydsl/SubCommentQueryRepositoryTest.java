package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.SubCommentListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.SubCommentEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.SubCommentRepository;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.SubComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @DisplayName("[happy] 대댓글이 성공적으로 삭제처리(del_yn = 'N') 된다.")
    @Test
    void deleteSubCommentSuccess() {
        // given
        Long subCommentId = 1L;
        // when
        sut.softDeleteSubComment(subCommentId);
        // then
        SubCommentEntity updatedEntity = subCommentRepository.findById(1L).get();
        assertEquals(updatedEntity.getDelYn(), "Y");
    }

    @DisplayName("[happy] parentCommentId에 해당하는 대댓글 목록을 페이징하여 조회한다.")
    @Test
    void getAllSubCommentListSuccess() {
        // given
        Long parentCommentId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<SubCommentListResponseDto> result = sut.getSubCommentDtoList(parentCommentId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull();
        result.getContent().forEach(subCommentDto -> {
            assertThat(subCommentDto.getId()).isNotNull();
            assertThat(subCommentDto.getSubCommentValue()).isNotNull();
            assertThat(subCommentDto.getMemberId()).isNotNull();
            assertThat(subCommentDto.getNickname()).isNotNull();
        });
    }

    @DisplayName("[happy] commentId에 해당하는 대댓글이 존재할때 soft delete한다.")
    @Test
    void findAllByRecipeEntity_Id() {
        // given
        List<Long> commentIds = List.of(1L);
        // when
        sut.softDeleteSubCommentByCommentIds(commentIds);
        // then
        List<SubCommentEntity> allByCommentEntityId = subCommentRepository.findAllByCommentEntity_Id(commentIds.get(0));
        assertEquals(allByCommentEntityId.get(0).getDelYn(), "Y");
    }
}
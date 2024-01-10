package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.CommentQueryRepository;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.converter.CommentConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentAdapter implements CommentPort {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final CommentQueryRepository commentQueryRepository;

    /**
     * [CREATE] 댓글 저장
     * 저장에 성공하면 생성된 댓글의 pk값을 반환한다.
     */
    @Override
    public Long createComment(Comment comment) {
        CommentEntity commentEntity = commentConverter.domainToEntity(comment);
        return commentRepository.save(commentEntity).getId();
    }

    /**
     * [UPDATE] 댓글 수정
     * 댓글 수정에 성공하면 업데이트 된 row의 갯수를 반환한다.
     */
    @Override
    public Long updateComment(Comment comment) {
        return commentQueryRepository.updateComment(comment);
    }

    /**
     * [READ] commentId, memberId로 댓글 검색
     * 조건에 해당하는 댓글을 검색해서 댓글이 존재하면 true, 존재하지 않으면 false를 반환한다.
     */
    @Override
    public boolean checkIsCommentExist(Comment comment) {
        Optional<CommentEntity> commentEntity = commentRepository.findByIdAndMemberIdAndDelYn(comment.getId(), comment.getMemberId(), comment.getDelYn());
        return commentEntity.isPresent();
    }

    /**
     * [DELETE] 댓글 삭제
     * 댓글 삭제(del_yn = "Y"로 컬럼 update)에 성공하면 업데이트 된 row의 갯수를 반환한다.
     */
    @Override
    public Long softDeleteComment(Comment comment) {
        return commentQueryRepository.softDeleteComment(comment.getId());
    }

}
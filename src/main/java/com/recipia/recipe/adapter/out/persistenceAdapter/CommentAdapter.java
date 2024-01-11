package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.CommentQueryRepository;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.converter.CommentConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * [READ] commentId, memberId, del_yn으로 댓글 검색
     * 조건에 해당하는 댓글을 검색해서 댓글이 존재하면 true, 존재하지 않으면 false를 반환한다.
     */
    @Override
    public boolean checkIsCommentExistAndMine(Comment comment) {
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

    /**
     * [READ] recipeId에 해당하는 댓글 목록 조회
     * querydsl을 사용해서 댓글 목록 조회 최적화 (목록, count)
     */
    @Override
    public Page<CommentListResponseDto> getCommentList(Long recipeId, Pageable pageable, String sortType) {
        Page<CommentEntity> commentEntities = commentQueryRepository.getCommentEntityList(recipeId, pageable, sortType);

        List<CommentListResponseDto> dtoList = commentEntities.getContent().stream()
                .map(entity -> CommentListResponseDto.of(
                        entity.getId(),
                        entity.getMemberId(),
                        null,
                        entity.getCommentText(),
                        entity.getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        !entity.getCreateDateTime().isEqual(entity.getUpdateDateTime())
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, commentEntities.getTotalElements());
    }

}

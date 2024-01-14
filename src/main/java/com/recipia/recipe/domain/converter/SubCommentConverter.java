package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.SubCommentDeleteRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.SubCommentRegistRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.SubCommentUpdateRequestDto;
import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistence.entity.SubCommentEntity;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.SubComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SubCommentConverter {

    private final SecurityUtil securityUtil;

    public SubCommentEntity domainToEntity(SubComment domain) {
        CommentEntity commentEntity = CommentEntity.builder().id(domain.getParentCommentId()).build();
        return SubCommentEntity.of(
                commentEntity,
                domain.getMemberId(),
                domain.getSubCommentText(),
                domain.getDelYn()
        );
    }

    public SubComment registRequestDtoToDomain(SubCommentRegistRequestDto dto) {
        Long memberId = securityUtil.getCurrentMemberId();
        return SubComment.of(dto.getParentCommentId(), memberId, dto.getSubCommentText(), "N");
    }

    public SubComment updateRequestDtoToDomain(SubCommentUpdateRequestDto dto) {
        Long memberId = securityUtil.getCurrentMemberId();
        return SubComment.of(dto.getId(), memberId, dto.getSubCommentText(), "N");
    }

    public SubComment deleteRequestDtoToDomain(SubCommentDeleteRequestDto dto) {
        Long memberId = securityUtil.getCurrentMemberId();
        return SubComment.of(dto.getId(), dto.getParentCommentId(), memberId, null, "Y");
    }

}

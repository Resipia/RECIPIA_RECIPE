package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.CommentRequestDto;
import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentConverter {

    private final SecurityUtil securityUtil;

    public CommentEntity domainToEntity(Comment domain) {
        RecipeEntity recipeEntity = RecipeEntity.of(domain.getRecipeId());
        return CommentEntity.of(
                recipeEntity,
                domain.getMemberId(),
                domain.getCommentText(),
                domain.getDelYn()
        );
    }

    public Comment dtoToDomain(CommentRequestDto dto) {
        Long memberId = securityUtil.getCurrentMemberId();

        return Comment.of(dto.getRecipeId(), memberId, dto.getCommentText(), "N");
    }

}

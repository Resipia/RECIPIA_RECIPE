package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeLikeRequestDto;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeLikeEntity;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 좋아요 컨버터
 */
@RequiredArgsConstructor
@Component
public class RecipeLikeConverter {

    private final SecurityUtil securityUtil;

    /**
     * 좋아요 도메인을 엔티티로 변환한다.
     */
    public RecipeLikeEntity domainToEntity(RecipeLike domain) {
        return (domain.getId() == null)
                ? RecipeLikeEntity.of( // null이면 동작
                RecipeEntity.of(domain.getRecipe().getId()),
                domain.getMemberId())

                : RecipeLikeEntity.of( // 존재하면 동작
                domain.getId(),
                RecipeEntity.of(domain.getRecipe().getId()),
                domain.getMemberId()
        );
    }

    /**
     * dto를 도메인 객체로 변환한다.
     */
    public RecipeLike dtoToDomain(RecipeLikeRequestDto dto) {
        // 좋아요 ID가 null인 경우 처리
        Long currentMemberId = securityUtil.getCurrentMemberId();
        return (dto.getRecipeLikeId() == null)
                ? RecipeLike.of(Recipe.of(dto.getRecipeId()), currentMemberId)
                : RecipeLike.of(dto.getRecipeLikeId(), Recipe.of(dto.getRecipeId()), currentMemberId);
    }

}

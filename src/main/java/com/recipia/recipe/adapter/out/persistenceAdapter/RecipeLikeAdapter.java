package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeLikeEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeLikeQuerydslRepository;
import com.recipia.recipe.application.port.out.RecipeLikePort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.RecipeLike;
import com.recipia.recipe.domain.converter.RecipeLikeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 좋아요 기능과 관련된 어댑터
 */
@RequiredArgsConstructor
@Repository
public class RecipeLikeAdapter implements RecipeLikePort {

    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeLikeConverter recipeLikeConverter;
    private final RecipeRepository recipeRepository;
    private final RecipeLikeQuerydslRepository recipeLikeQuerydslRepository;

    /**
     * [CREATE]
     * 좋아요를 저장하면 저장된 좋아요 엔티티의 id값이 반환된다.
     */
    @Override
    public Long saveLike(RecipeLike domain) {
        boolean existsRecipe = recipeRepository.existsById(domain.getRecipe().getId());
        if (!existsRecipe) {
            throw new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND);
        }

        RecipeLikeEntity entity = recipeLikeConverter.domainToEntity(domain);
        return recipeLikeRepository.save(entity).getId();
    }

    /**
     * [DELETE]
     * 좋아요를 삭제한다.
     */
    @Override
    public void deleteRecipeLike(RecipeLike domain) {

        // 1. 레시피가 존재하는지 검증
        validRecipe(domain);

        // 2. 좋아요 존재 여부 확인
        validRecipeLike(domain);

        // 3. 좋아요 삭제
        RecipeLikeEntity entity = recipeLikeConverter.domainToEntity(domain);
        recipeLikeRepository.delete(entity);
    }

    /**
     * [DELETE] recipeId에 해당하는 좋아요 데이터를 삭제한다.
     */
    @Override
    public Long deleteRecipeLikeByRecipeId(Long recipeId) {
        return recipeLikeQuerydslRepository.deleteLikeByRecipeId(recipeId);
    }

    /**
     * [DELETE] memberId에 해당하는 좋아요를 삭제한다.
     */
    @Override
    public Long deleteLikeByMemberId(Long memberId) {
        return recipeLikeQuerydslRepository.deleteLikeByMemberId(memberId);
    }

    /**
     * [EXTRACT]
     * 1. 좋아요 엔티티의 존재 여부 검증
     * 2. 등록/삭제하려는 유저의 좋아요가 맞는지 검증한다.
     */
    public void validRecipeLike(RecipeLike domain) {
        boolean existsRecipeLike = recipeLikeRepository.existsByIdAndMemberId(domain.getId(), domain.getMemberId());
        if (!existsRecipeLike) {
            throw new RecipeApplicationException(ErrorCode.NOT_FOUND_LIKE);
        }
    }

    /**
     * [EXTRACT]
     * 1. 레시피 정보를 검증한다.
     */
    public void validRecipe(RecipeLike domain) {
        boolean existsRecipe = recipeRepository.existsById(domain.getRecipe().getId());
        if (!existsRecipe) {
            throw new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND);
        }
    }

}

package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeRequestDto;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.domain.Recipe;

/**
 * domain, entity, dto 서로간의 의존성을 제거하기 위해 Converter 클래스를 작성
 */
public class RecipeConverter {


    /**
     * Recipe 엔티티를 받아서 Recipe 도메인으로 변환
     */
    // entity to domain
    public static Recipe entityToDomain(RecipeEntity entity) {
        return Recipe.of(
            entity.getId(),
            entity.getMemberId(),
            entity.getRecipeName(),
            entity.getRecipeDesc(),
            entity.getTimeTaken(),
            entity.getIngredient(),
            entity.getHashtag(),
            entity.getNutritionalInfo(),
            entity.getNickname(),
            entity.getDelYn()
        );
    }

    /**
     * Recipe 도메인을 받아서 RecipeEntity 엔티티로 변환
     */
    // entity to domain
    public static RecipeEntity domainToEntity(Recipe domain) {
        return RecipeEntity.of(
            domain.getId(),
            domain.getMemberId(),
            domain.getRecipeName(),
            domain.getRecipeDesc(),
            domain.getTimeTaken(),
            domain.getIngredient(),
            domain.getHashtag(),
            domain.getNutritionalInfo(),
            domain.getNickname(),
            domain.getDelYn()
        );
    }

    /**
     * RequestDto를 도메인으로 반환
     */
    // entity to domain
    public static Recipe dtoToDomain(RecipeRequestDto dto) {
        return Recipe.of(
                dto.getId(),
                null,
                dto.getRecipeName(),
                dto.getRecipeDesc(),
                dto.getTimeTaken(),
                dto.getIngredient(),
                dto.getHashtag(),
                dto.getNutritionalInfo(),
                null,
                dto.getDelYn()
        );
    }

}

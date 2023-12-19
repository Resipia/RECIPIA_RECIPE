package com.recipia.recipe.hexagonal.domain.converter;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
import com.recipia.recipe.hexagonal.domain.Recipe;

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
            entity.getNickname(),
            entity.getDelYn(
            ));
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
            domain.getNickname(),
            domain.getDelYn(
        ));
    }

}

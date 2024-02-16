package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeViewCountEntity.recipeViewCountEntity;

@RequiredArgsConstructor
@Repository
public class RecipeViewCountQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    /**
     *  recipeId와 viewCount를 인자로 받아 해당 recipeId에 해당하는 레코드의 viewCount 필드를 업데이트한다.
     */
    public void updateViewCountInDatabase(Long recipeId, Integer viewCount) {

        queryFactory.update(recipeViewCountEntity)
                .where(recipeViewCountEntity.recipeEntity.id.eq(recipeId))
                .set(recipeViewCountEntity.viewCount, viewCount)
                .execute();
    }

}

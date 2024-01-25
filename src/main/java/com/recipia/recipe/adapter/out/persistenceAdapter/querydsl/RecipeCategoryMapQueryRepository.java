package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeCategoryMapEntity.recipeCategoryMapEntity;

@RequiredArgsConstructor
@Repository
public class RecipeCategoryMapQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * [DELETE] recipeId에 해당하는 레시피-서브카테고리 데이터 삭제
     */
    public Long deleteRecipeCategoryMapByRecipeId(Long recipeId) {
        return jpaQueryFactory
                .delete(recipeCategoryMapEntity)
                .where(recipeCategoryMapEntity.recipeEntity.id.eq(recipeId))
                .execute();
    }
}

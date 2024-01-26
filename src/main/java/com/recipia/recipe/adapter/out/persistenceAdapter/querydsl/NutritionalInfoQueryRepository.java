package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.recipia.recipe.adapter.out.persistence.entity.QNutritionalInfoEntity.nutritionalInfoEntity;

@RequiredArgsConstructor
@Repository
public class NutritionalInfoQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * [DELETE] recipeId에 해당하는 영양소 정보 삭제
     */
    public Long deleteNutritionalInfosInRecipeIds(List<Long> recipeIds) {
        return queryFactory
                .delete(nutritionalInfoEntity)
                .where(nutritionalInfoEntity.recipe.id.in(recipeIds))
                .execute();
    }
}

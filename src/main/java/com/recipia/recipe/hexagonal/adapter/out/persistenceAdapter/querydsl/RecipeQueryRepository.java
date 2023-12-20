package com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.recipia.recipe.hexagonal.adapter.out.persistence.QRecipeEntity.recipeEntity;

@RequiredArgsConstructor
@Repository
public class RecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

    public RecipeEntity findById(Long id) {
        return queryFactory.selectFrom(recipeEntity)
                .where(recipeEntity.id.eq(id))
                .fetchOne();
    }
}

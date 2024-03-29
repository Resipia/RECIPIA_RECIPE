package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeLikeEntity.recipeLikeEntity;

@RequiredArgsConstructor
@Repository
public class RecipeLikeQuerydslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * [DELETE] recipeId에 해당하는 좋아요를 삭제한다.
     */
    public Long deleteLikesInRecipeIds(List<Long> recipeIds) {
        return jpaQueryFactory
                .delete(recipeLikeEntity)
                .where(recipeLikeEntity.recipeEntity.id.in(recipeIds))
                .execute();
    }

    /**
     * [DELETE] memberId에 해당하는 좋아요를 삭제한다.
     */
    public Long deleteLikeByMemberId(Long memberId) {
        return jpaQueryFactory
                .delete(recipeLikeEntity)
                .where(recipeLikeEntity.memberId.eq(memberId))
                .execute();
    }
}

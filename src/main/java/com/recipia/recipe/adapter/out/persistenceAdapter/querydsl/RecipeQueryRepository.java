package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.recipia.recipe.adapter.out.persistence.QRecipeEntity.recipeEntity;


@RequiredArgsConstructor
@Repository
public class RecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 유저가 변경한 닉네임을 레시피의 모든 엔티티에서도 변경시켜준다.
     */
    public long updateRecipesNicknames(NicknameDto nicknameDto) {
        // updatedCount에는 업데이트된 레코드의 수가 저장된다.
        return queryFactory
                .update(recipeEntity)
                .set(recipeEntity.nickname, nicknameDto.nickname())
                .where(recipeEntity.memberId.eq(nicknameDto.memberId()))
                .execute();
    }

}

package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.recipia.recipe.adapter.out.persistence.entity.QBookmarkEntity.bookmarkEntity;

@RequiredArgsConstructor
@Repository
public class BookmarkQuerydslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * [DELETE] recipeId에 해당하는 북마크를 삭제한다.
     */
    public Long deleteBookmarkByRecipeId(Long recipeId) {
        return jpaQueryFactory
                .delete(bookmarkEntity)
                .where(bookmarkEntity.recipeEntity.id.eq(recipeId))
                .execute();
    }

}

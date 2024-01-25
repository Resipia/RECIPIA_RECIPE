package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.out.persistence.entity.BookmarkEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeLikeEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.RecipeLikeRepository;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DisplayName("[통합] 좋아요 레시피 querydsl 테스트")
class RecipeLikeQuerydslRepositoryTest extends TotalTestSupport {

    @Autowired
    private RecipeLikeQuerydslRepository sut;
    @Autowired
    private RecipeLikeRepository recipeLikeRepository;


    @DisplayName("[happy] recipeId에 해당하는 좋아요를 삭제한다.")
    @Test
    void deleteLikeByRecipeId() {
        // given
        Long recipeId = 1L;
        // when
        Long deletedCount = sut.deleteLikeByRecipeId(recipeId);
        // then
        List<RecipeLikeEntity> allByRecipeEntityId = recipeLikeRepository.findAllByRecipeEntity_Id(recipeId);
        assertThat(allByRecipeEntityId.size()).isEqualTo(0);
    }

}
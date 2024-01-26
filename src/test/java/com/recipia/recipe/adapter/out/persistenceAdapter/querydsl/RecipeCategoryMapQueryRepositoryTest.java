package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeCategoryMapEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.RecipeCategoryMapRepository;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[통합] 레시피-카테고리 맵핑 querydsl 테스트")
class RecipeCategoryMapQueryRepositoryTest extends TotalTestSupport {

    @Autowired
    private RecipeCategoryMapQueryRepository sut;
    @Autowired
    private RecipeCategoryMapRepository recipeCategoryMapRepository;

    @DisplayName("[happy] recipeId에 해당하는 레시피-카테고리 맵핑 데이터를 삭제한다.")
    @Test
    void deleteRecipeCategoryMapsInRecipeIds() {
        // given
        List<Long> recipeIds = List.of(1L);
        // when
        sut.deleteRecipeCategoryMapsInRecipeIds(recipeIds);
        // then
        List<RecipeCategoryMapEntity> recipeCategoryMapEntityList = recipeCategoryMapRepository.findByRecipeEntityId(recipeIds.get(0));
        assertThat(recipeCategoryMapEntityList).isEmpty();
    }

}
package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.NutritionalInfoRepository;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[통합] 영양소 정보 querydls 테스트")
class NutritionalInfoQueryRepositoryTest extends TotalTestSupport {

    @Autowired
    private NutritionalInfoQueryRepository sut;
    @Autowired
    private NutritionalInfoRepository nutritionalInfoRepository;

    @DisplayName("[happy] recipeId에 해당하는 영양소 정보를 삭제한다.")
    @Test
    void deleteNutritionalInfo() {
        // given
        List<Long> recipeIds = List.of(1L);
        // when
        Long deletedCount = sut.deleteNutritionalInfosInRecipeIds(recipeIds);
        // then
        Optional<NutritionalInfoEntity> result = nutritionalInfoRepository.findByRecipe_Id(recipeIds.get(0));
        assertThat(result).isEmpty();
    }

}
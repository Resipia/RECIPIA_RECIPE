package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.core.Tuple;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.NutritionalInfoRepository;
import com.recipia.recipe.adapter.out.persistenceAdapter.RecipeRepository;
import com.recipia.recipe.config.TotalTestSupport;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("[통합] 레시피 queryDsl 테스트")
class RecipeQueryRepositoryTest extends TotalTestSupport {

    @Autowired
    private RecipeQueryRepository sut;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private NutritionalInfoRepository nutritionalInfoRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("[happy] 닉네임이 성공적으로 업데이트된다.")
    @Test
    void updateRecipesNicknamesTest() {
        //given
        NicknameDto nicknameDto = new NicknameDto(1L, "새로운 닉네임");

        //when
        sut.updateRecipesNicknames(nicknameDto);

        //then
        RecipeEntity updatedRecipe = recipeRepository.findById(1L).orElseThrow();
        assertThat(updatedRecipe.getNickname()).isEqualTo("새로운 닉네임");
    }


    @DisplayName("[happy] 전체 레시피 목록을 페이징하여 조회한다.")
    @Test
    void getAllRecipeListTest() {
        //given
        Long memberId = 1L; // 예시로 사용할 멤버 ID
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";

        //when
        Page<RecipeMainListResponseDto> result = sut.getAllRecipeList(memberId, pageable, sortType);

        //then
        assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isNotEmpty();
        result.getContent().forEach(recipe -> {
            assertThat(recipe.getId()).isNotNull();
            assertThat(recipe.getRecipeName()).isNotNull();
            assertThat(recipe.getNickname()).isNotNull();
            // 북마크 여부는 memberId에 따라 다를 수 있으므로, 테스트 케이스 작성시 주의 필요
        });
    }

    @DisplayName("[happy] 존재하지 않는 레시피 ID에 대한 조회는 null을 반환해야 한다.")
    @Test
    void getRecipeDetailViewWithInvalidRecipeIdReturnsNullTest() {
        //given
        Long invalidRecipeId = 9999L; // 존재하지 않는 레시피 ID
        Long memberId = 1L;

        //when
        Optional<RecipeDetailViewDto> recipeDetailView = sut.getRecipeDetailView(invalidRecipeId, memberId);

        //then
        Assertions.assertThat(recipeDetailView).isEmpty();
    }

    @Test
    @DisplayName("[happy] 수정할 데이터를 받아서 레시피를 업데이트 하면 레시피id를 반환한다.")
    void updateRecipeTest() {
        //given
        Long recipeId = 1L;
        RecipeEntity updatedRecipe = RecipeEntity.builder()
                .id(recipeId)
                .recipeName("업데이트 이름")
                .recipeDesc("업데이트 설명")
                .ingredient("업데이트 재료")
                .hashtag("업데이트 해시태그")
                .build();

        //when
        sut.updateRecipe(updatedRecipe);
        entityManager.flush();
        entityManager.clear();

        //then
        RecipeEntity result = recipeRepository.findById(recipeId).orElseThrow();
        assertThat(result.getRecipeName()).isEqualTo(updatedRecipe.getRecipeName());
        assertThat(result.getRecipeDesc()).isEqualTo(updatedRecipe.getRecipeDesc());
    }

    @Test
    @DisplayName("[bad] 존재하지 않는 레시피id로 업데이트를 시도하면 0을 반환한다.")
    void updateRecipeFailTest() {
        //given
        Long recipeId = 100L;
        RecipeEntity updatedRecipe = RecipeEntity.builder()
                .id(recipeId)
                .recipeName("업데이트 이름")
                .recipeDesc("업데이트 설명")
                .ingredient("업데이트 재료")
                .hashtag("업데이트 해시태그")
                .build();

        //when
        Long updateRecipeId = sut.updateRecipe(updatedRecipe);
        entityManager.flush();
        entityManager.clear();

        //then
        Assertions.assertThat(updateRecipeId).isEqualTo(0);
    }

    @Test
    @DisplayName("[happy] 영양소 업데이트에 성공하면 데이터베이스에 반영된다.")
    void updateNutritionalInfoTest() {
        // given
        Long recipeId = 1L;
        Long nutritionalInfoId = 1L;
        NutritionalInfoEntity updateEntity = NutritionalInfoEntity.of(
                nutritionalInfoId,
                30,
                30,
                40,
                50,
                100,
                RecipeEntity.of(recipeId)
        );

        // when
        sut.updateNutritionalInfo(updateEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        NutritionalInfoEntity result = nutritionalInfoRepository.findById(nutritionalInfoId).orElseThrow();
        Assertions.assertThat(result.getCarbohydrates()).isEqualTo(30);
        Assertions.assertThat(result.getProtein()).isEqualTo(30);
        Assertions.assertThat(result.getMinerals()).isEqualTo(100);
    }

    @Test
    @DisplayName("[bad] 존재하지 않는 영양소id로 업데이트를 시도하면 예외가 발생한다.")
    void updateNutritionalInfoFailTest() {
        // given
        Long recipeId = 1L;
        Long nutritionalInfoId = 100L;
        NutritionalInfoEntity updateEntity = NutritionalInfoEntity.of(
                nutritionalInfoId,
                30,
                30,
                40,
                50,
                100,
                RecipeEntity.of(recipeId)
        );

        //when
        Long updateRecipeId = sut.updateNutritionalInfo(updateEntity);
        entityManager.flush();
        entityManager.clear();

        //then
        Assertions.assertThat(updateRecipeId).isEqualTo(0);
    }



}
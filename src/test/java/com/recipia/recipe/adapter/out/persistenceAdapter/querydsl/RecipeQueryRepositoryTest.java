package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.core.Tuple;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.RecipeRepository;
import com.recipia.recipe.config.TotalTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("[통합] 레시피 queryDsl 테스트")
class RecipeQueryRepositoryTest extends TotalTestSupport {

    @Autowired
    private RecipeQueryRepository sut;

    @Autowired
    private RecipeRepository recipeRepository; // 실제 데이터베이스와 상호작용

    @DisplayName("[happy] 닉네임이 성공적으로 업데이트된다.")
    @Test
    void updateRecipesNicknamesTest() {
        // Given
        NicknameDto nicknameDto = new NicknameDto(1L, "새로운 닉네임");

        // When
        sut.updateRecipesNicknames(nicknameDto);

        // Then
        RecipeEntity updatedRecipe = recipeRepository.findById(1L).orElseThrow();
        assertThat(updatedRecipe.getNickname()).isEqualTo("새로운 닉네임");
    }


    @DisplayName("[happy] 서브 카테고리 목록이 올바르게 반환된다.")
    @Test
    void getSubCategoryNameListTupleTest() {
        // Given
        List<Long> recipeIds = List.of(1L); // 실제 데이터베이스에 존재하는 레시피 ID를 사용해야 함

        // When
        List<Tuple> subCategoryNameList = sut.findSubCategoriesForRecipe(recipeIds);

        // Then
        Assertions.assertThat(subCategoryNameList).isNotNull().isNotEmpty();
        for (Tuple tuple : subCategoryNameList) {
            Long recipeId = tuple.get(0, Long.class);
            String subCategoryName = tuple.get(1, String.class);
            Assertions.assertThat(recipeIds).contains(recipeId);
            assertThat(subCategoryName).isNotNull();
        }
    }

    @DisplayName("[happy] 전체 레시피 목록을 페이징하여 조회한다.")
    @Test
    void getAllRecipeListTest() {
        // Given
        Long memberId = 1L; // 예시로 사용할 멤버 ID
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";

        // When
        Page<RecipeMainListResponseDto> result = sut.getAllRecipeList(memberId, pageable, sortType);

        // Then
        assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isNotEmpty();
        result.getContent().forEach(recipe -> {
            assertThat(recipe.getId()).isNotNull();
            assertThat(recipe.getRecipeName()).isNotNull();
            assertThat(recipe.getNickname()).isNotNull();
            // 북마크 여부는 memberId에 따라 다를 수 있으므로, 테스트 케이스 작성시 주의 필요
        });
    }

    @DisplayName("[happy] 여러 레시피에 대한 서브 카테고리 목록이 올바르게 반환된다.")
    @Test
    void findSubCategoriesForMultipleRecipesTest() {
        // Given
        List<Long> recipeIds = List.of(1L, 2L); // 존재하는 레시피 ID 사용

        // When
        List<Tuple> subCategoryNameList = sut.findSubCategoriesForRecipe(recipeIds);

        // Then
        Assertions.assertThat(subCategoryNameList).isNotNull().isNotEmpty();
        Assertions.assertThat(subCategoryNameList).allSatisfy(tuple -> {
            Long recipeId = tuple.get(0, Long.class);
            String subCategoryName = tuple.get(1, String.class);
            Assertions.assertThat(recipeIds).contains(recipeId);
            assertThat(subCategoryName).isNotNull();
        });
    }

    @DisplayName("[happy] 단일 레시피에 대한 서브 카테고리 목록이 올바르게 반환된다.")
    @Test
    void findSubCategoriesForSingleRecipeTest() {
        // Given
        Long recipeId = 1L; // 존재하는 레시피 ID 사용

        // When
        List<Tuple> subCategoryNameList = sut.findSubCategoriesForRecipe(recipeId);

        // Then
        Assertions.assertThat(subCategoryNameList).isNotNull().isNotEmpty();
        Assertions.assertThat(subCategoryNameList).allSatisfy(tuple -> {
            Long id = tuple.get(0, Long.class);
            String subCategoryName = tuple.get(1, String.class);
            assertThat(id).isEqualTo(recipeId);
            assertThat(subCategoryName).isNotNull();
        });
    }

    @DisplayName("[happy] 단일 레시피 상세 정보가 올바르게 반환된다.")
    @Test
    void getRecipeDetailViewTest() {
        // Given
        Long recipeId = 1L; // 존재하는 레시피 ID 사용
        Long memberId = 1L; // 존재하는 멤버 ID 사용

        // When
        RecipeDetailViewDto detailView = sut.getRecipeDetailView(recipeId, memberId);

        // Then
        assertThat(detailView).isNotNull();
        assertThat(detailView.getId()).isEqualTo(recipeId);
        assertThat(detailView.getRecipeName()).isNotNull();
        assertThat(detailView.getNickname()).isNotNull();
        assertThat(detailView.getRecipeDesc()).isNotNull();
        // 북마크 여부 검증
    }

    @DisplayName("[happy] 존재하지 않는 레시피 ID에 대한 조회는 null을 반환해야 한다.")
    @Test
    void getRecipeDetailViewWithInvalidRecipeIdReturnsNullTest() {
        // Given
        Long invalidRecipeId = 9999L; // 존재하지 않는 레시피 ID
        Long memberId = 1L;

        // When
        RecipeDetailViewDto result = sut.getRecipeDetailView(invalidRecipeId, memberId);

        // Then
        assertThat(result).isNull();
    }

}
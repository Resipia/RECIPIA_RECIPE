package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.NutritionalInfoRepository;
import com.recipia.recipe.adapter.out.persistenceAdapter.RecipeFileRepository;
import com.recipia.recipe.adapter.out.persistenceAdapter.RecipeRepository;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Recipe;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@DisplayName("[통합] 레시피 queryDsl 테스트")
@Transactional
class RecipeQueryRepositoryTest extends TotalTestSupport {

    @Autowired
    private RecipeQueryRepository sut;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private NutritionalInfoRepository nutritionalInfoRepository;

    @Autowired
    private RecipeFileRepository recipeFileRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("[happy] 서브 카테고리를 선택하지 않은 상황에서 전체 레시피 목록을 페이징하여 조회한다.")
    @Test
    void getAllRecipeListTest() {
        //given
        Long memberId = 1L; // 예시로 사용할 멤버 ID
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";
        List<Long> subCategoryList = List.of();

        //when
        Page<RecipeListResponseDto> result = sut.getAllRecipeList(memberId, pageable, sortType, subCategoryList, null);

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

    @DisplayName("[happy] 검색어가 있는 상황에서 전체 레시피 목록을 페이징하여 조회한다.")
    @Test
    void getAllRecipeListSearchWorldTest() {
        //given
        Long memberId = 1L; // 예시로 사용할 멤버 ID
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";
        List<Long> subCategoryList = List.of();
        String searchWorld = "김치찌개";

        //when
        Page<RecipeListResponseDto> result = sut.getAllRecipeList(memberId, pageable, sortType, subCategoryList, searchWorld);

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

    @DisplayName("[happy] 서브 카테고리를 선택한 상황에서 전체 레시피 목록을 페이징하여 조회한다.")
    @Test
    void getAllRecipeListTestWithSubCategory() {
        //given
        Long memberId = 1L; // 예시로 사용할 멤버 ID
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";
        List<Long> subCategoryList = List.of(1L);

        //when
        Page<RecipeListResponseDto> result = sut.getAllRecipeList(memberId, pageable, sortType, subCategoryList, null);

        //then
        assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isNotEmpty();
        result.getContent().forEach(recipe -> {
            assertThat(recipe.getId()).isNotNull();
            assertThat(recipe.getRecipeName()).isNotNull();
            assertThat(recipe.getNickname()).isNotNull();
            assertThat(recipe.getSubCategoryList().contains(1L));
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
        Optional<Recipe> recipeDetailView = sut.getRecipeDetailView(invalidRecipeId, memberId);

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

    @DisplayName("[happy] 레시피 파일 삭제를 시도하면 Soft Delete에 성공하여 del_yn이 'Y'가 된다.")
    @Test
    void softDeleteRecipeFilesByRecipeId_Success() {
        // given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        Long recipeId = savedRecipeEntity.getId();
        Recipe domain = Recipe.of(recipeId, 1L);

        RecipeFileEntity recipeFileEntity = RecipeFileEntity.of(savedRecipeEntity, 1, "/", "url", "nm", "nm2", "jpg", 100, "N");
        RecipeFileEntity savedFileEntity = recipeFileRepository.save(recipeFileEntity);

        // when
        sut.softDeleteRecipeFile(domain, List.of(1));
        entityManager.flush();
        entityManager.clear();

        // then
        List<RecipeFileEntity> deletedFileList = recipeFileRepository.findAllSoftDeletedFileList(recipeId);
        Assertions.assertThat(deletedFileList).isNotEmpty();
        Assertions.assertThat(deletedFileList).allMatch(file -> file.getDelYn().equals("Y"));
    }

    @DisplayName("[bad] 존재하지 않는 레시피 ID로 파일 삭제를 시도하면 업데이트 count로 0을 반환한다.")
    @Test
    void softDeleteRecipeFilesByInvalidRecipeId() {
        // given
        Long invalidRecipeId = 9999L; // 존재하지 않는 레시피 ID
        Recipe domain = Recipe.of(invalidRecipeId, 1L);

        // when
        Long updatedCount = sut.softDeleteRecipeFile(domain, List.of(1));

        // then
        assertThat(updatedCount).isEqualTo(0); // 업데이트된 행이 없어야 함
    }

    @DisplayName("[happy] 레시피 삭제를 시도하면 Soft Delete에 성공하여 del_yn이 'Y'가 된다.")
    @Test
    void softDeleteRecipeByRecipeId_Success() {
        // given
        Long recipeId = 1L; // 테스트 대상 레시피 ID

        // when
        Long result = sut.softDeleteRecipeByRecipeId(recipeId);

        // then
        assertThat(result).isGreaterThan(0); // 실제로 업데이트된 행의 수 검증
        Optional<RecipeEntity> deletedRecipe = recipeRepository.findById(recipeId);
        assertThat(deletedRecipe).isPresent();
        assertThat(deletedRecipe.get().getDelYn()).isEqualTo("Y");
    }

    @DisplayName("[bad] 존재하지 않는 레시피 ID로 레시피 삭제를 시도하면 0을 반환한다.")
    @Test
    void softDeleteRecipeByInvalidRecipeId() {
        // given
        Long invalidRecipeId = 9999L; // 존재하지 않는 레시피 ID

        // when
        Long result = sut.softDeleteRecipeByRecipeId(invalidRecipeId);

        // then
        assertThat(result).isEqualTo(0); // 업데이트된 행이 없어야 함
    }

    @DisplayName("[happy] 레시피 내부의 좋아요 갯수가 이미 존재할 때 업데이트 되면 0보다 큰 수를 반환한다.")
    @Test
    void updateLikesInDatabase() {
        //given
        Long recipeId = 1L;
        Integer likeCount = 10;

        //when
        Long updatedResult = sut.updateLikesInDatabase(recipeId, likeCount);

        //then
        Assertions.assertThat(updatedResult).isGreaterThan(0);
    }

    @DisplayName("[happy] 존재하지 않는 recipeId를 사용하여 업데이트 시도하면 0을 반환한다.")
    @Test
    void test() {
        //given
        Long recipeId = 9999L;
        Integer likeCount = 10;

        //when
        Long updatedResult = sut.updateLikesInDatabase(recipeId, likeCount);

        //then
        Assertions.assertThat(updatedResult).isEqualTo(0);
    }

    @DisplayName("[happy] targetMember가 작성한 레시피의 id를 목록으로 반환한다.")
    @Test
    void findTargetMemberRecipeIdsSuccess() {
        // given
        Long targetMemberId = 1L;
        // when
        List<Long> recipeIds = sut.findTargetMemberRecipeIds(targetMemberId);
        // then
        assertThat(recipeIds.size()).isEqualTo(2L);
    }

    @DisplayName("[happy] targetMember가 작성한 레시피 목록중에서 썸네일이 존재하면 썸네일 저장경로도 포함해서 데이터를 반환한다.")
    @Test
    void getTargetMemberHighRecipeListWithThumbnail() {
        // given
        Long targetMemberId = 1L;
        Long memberId = 2L;
        List<Long> highRecipeIds = List.of(1L, 2L);
        // when
        List<RecipeListResponseDto> result = sut.getTargetMemberHighRecipeList(targetMemberId, memberId, highRecipeIds);
        // then
        assertThat(result.get(0).getThumbnailFullPath()).isNotNull();
    }


    @DisplayName("[happy] targetMember가 작성한 레시피 목록중에서 썸네일이 존재하지 않으면 썸네일 저장경로를 포함하지 않은 데이터를 반환한다.")
    @Test
    void getTargetMemberHighRecipeListWithoutThumbnail() {
        // given
        Long targetMemberId = 1L;
        Long memberId = 2L;
        List<Long> highRecipeIds = List.of(1L, 2L);
        // when
        List<RecipeListResponseDto> result = sut.getTargetMemberHighRecipeList(targetMemberId, memberId, highRecipeIds);
        // then
        assertThat(result.get(1).getThumbnailFullPath()).isNull();
    }


    @DisplayName("[happy] targetMember가 작성한 레시피 목록을 가져온다.")
    @Test
    void getTargetRecipeListSuccess() {
        // given
        Long targetMemberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";
        Long memberId = 2L;

        // when
        Page<RecipeListResponseDto> allMyRecipeList = sut.getTargetRecipeList(targetMemberId, memberId, pageable, sortType);

        // then
        assertThat(allMyRecipeList).isNotNull();
    }

    @DisplayName("[happy] 내가 북마크한 레시피 목록을 가져온다.")
    @Test
    void getAllMyBookmarkList() {
        // given
        Long currentMemberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        // when
        Page<RecipeListResponseDto> allMyBookmarkRecipeList = sut.getAllMyBookmarkList(currentMemberId, pageable);
        // then
        assertThat(allMyBookmarkRecipeList).isNotNull();
        List<RecipeListResponseDto> content = allMyBookmarkRecipeList.getContent();
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(0).getBookmarkId()).isGreaterThanOrEqualTo(0L);
    }

    @DisplayName("[happy] recipeId에 해당하는 레시피 파일이 있을경우 0보다 큰 값을 리턴한다.")
    @Test
    void softDeleteRecipeFile() {
        // given
        List<Long> recipeIds = List.of(1L);
        // when
        Long updatedCount = sut.softDeleteRecipeFilesInRecipeIds(recipeIds);
        // then
        assertThat(updatedCount).isEqualTo(1L);
    }

    @DisplayName("[happy] recipeId에 해당하는 레시피 파일이 없을경우 0을 리턴한다.")
    @Test
    void softDeleteNonRecipeFile() {
        // given
        List<Long> recipeIds = List.of(4L);
        // when
        Long updatedCount = sut.softDeleteRecipeFilesInRecipeIds(recipeIds);
        // then
        assertThat(updatedCount).isEqualTo(0L);
    }

    @DisplayName("[happy] memberId가 작성한 레시피를 전부 soft delete 처리한다.")
    @Test
    void softDeleteRecipeByMemberId() {
        // given
        Long memberId = 1L;
        // when
        sut.softDeleteRecipeByMemberId(memberId);
        // then
        List<RecipeEntity> allByMemberId = recipeRepository.findAllByMemberId(memberId);
        assertTrue(allByMemberId.get(0).getDelYn().equals("Y"));
    }

    @DisplayName("[happy] memberId가 작성한 레시피 id를 성공적으로 반환한다.")
    @Test
    void getAllRecipeIdsByMemberId() {
        // given
        Long memberId = 1L;
        // when
        List<Long> allRecipeIdsByMemberId = sut.getAllRecipeIdsByMemberId(memberId);
        // then
        assertThat(allRecipeIdsByMemberId.size()).isEqualTo(2L);

    }

}
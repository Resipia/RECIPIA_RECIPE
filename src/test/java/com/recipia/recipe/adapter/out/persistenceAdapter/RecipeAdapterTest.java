package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.in.web.dto.request.NutritionalInfoDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeCategoryMapEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.config.TestJwtConfig;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.SubCategory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[통합] 레시피 Adapter 테스트")
class RecipeAdapterTest extends TotalTestSupport {

    @Autowired
    private RecipeAdapter sut;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private NutritionalInfoRepository nutritionalInfoRepository;

    @Autowired
    private RecipeCategoryMapRepository recipeCategoryMapRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RecipeFileRepository recipeFileRepository;


    @DisplayName("[happy] 유저가 레시피 저장에 성공하면 생성된 레시피의 id가 반환된다.")
    @Transactional
    @Test
    public void createRecipe() {
        //given
        Recipe domain = createRecipeDomain(1L, List.of(SubCategory.of(1L), SubCategory.of(2L)));

        //when
        Long recipeId = sut.createRecipe(domain);

        //then
        assertThat(recipeId).isNotNull();
        Optional<RecipeEntity> savedRecipe = recipeRepository.findById(recipeId);
        assertThat(savedRecipe.isPresent()).isTrue();
    }

    @DisplayName("[happy] 새롭게 저장할 레시피의 영양소 정보 저장에 성공하면, 저장된 정보의 ID를 반환한다.")
    @Test
    public void createNutritionalInfo() {
        // given
        Recipe domain = createRecipeDomain(1L, List.of(SubCategory.of(1L), SubCategory.of(2L)));
        Long recipeId = sut.createRecipe(domain);

        // when
        Long nutritionalInfoId = sut.createNutritionalInfo(domain, recipeId);

        // then
        assertThat(nutritionalInfoId).isNotNull();
        Optional<NutritionalInfoEntity> savedNutritionalInfo = nutritionalInfoRepository.findById(nutritionalInfoId);
        assertThat(savedNutritionalInfo.isPresent()).isTrue();
    }

    @DisplayName("[happy] 카테고리 저장을 시도하면 성공한다.")
    @Test
    void createRecipeCategoryMap() {
        //given
        Recipe domain = createRecipeDomain(1L, List.of(SubCategory.of(1L), SubCategory.of(2L)));
        Long recipeId = sut.createRecipe(domain);

        //when
        sut.createRecipeCategoryMap(domain, recipeId);

        //then
        List<RecipeCategoryMapEntity> result = recipeCategoryMapRepository.findAll(); // 기존3개 존재 + 테스트로 2개 추가 = 5
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(5);
    }

    @DisplayName("[bad] 카테고리 리스트가 null이라면 예외가 발생한다.")
    @Test
    void createRecipeCategoryMapExceptionNull() {
        //given
        Recipe domain = createRecipeDomain(1L, null);
        Long recipeId = sut.createRecipe(domain);

        //when & then
        assertThatThrownBy(() -> sut.createRecipeCategoryMap(domain, recipeId))
                .hasMessageContaining("카테고리는 null이거나 공백이어서는 안됩니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SUB_CATEGORY_IS_NULL_OR_EMPTY);
    }

    @DisplayName("[bad] 카테고리 리스트가 비어있다면 비어있다면 예외가 발생한다.")
    @Test
    void createRecipeCategoryMapExceptionEmpty() {
        //given
        Recipe domain = createRecipeDomain(1L, Collections.EMPTY_LIST);
        Long recipeId = sut.createRecipe(domain);

        //when & then
        assertThatThrownBy(() -> sut.createRecipeCategoryMap(domain, recipeId))
                .hasMessageContaining("카테고리는 null이거나 공백이어서는 안됩니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SUB_CATEGORY_IS_NULL_OR_EMPTY);
    }

    @DisplayName("[bad] 존재하지 않는 카테고리 저장을 시도하면 예외가 발생한다.")
    @Test
    void createRecipeCategoryMapException() {
        //given
        Recipe domain = createRecipeDomain(1L, List.of(SubCategory.of(50L)));
        Long recipeId = sut.createRecipe(domain);

        //when & then
        assertThatThrownBy(() -> sut.createRecipeCategoryMap(domain, recipeId))
                .hasMessageContaining("존재하지 않는 서브 카테고리입니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SUB_CATEGORY_NOT_EXIST);
    }

    @DisplayName("[happy] 유효한 페이지 정보와 정렬 유형이 주어지면, 페이지화된 레시피 목록과 서브 카테고리 정보를 반환한다.")
    @Test
    void getAllRecipeListTest() {
        // given
        Long expectedMemberId = 1L;
        String dummyNickname = "진아";
        TestJwtConfig.setupMockJwt(expectedMemberId, dummyNickname);
        Pageable pageable = PageRequest.of(0, 10);
        String sortType = "new";

        // when
        Page<RecipeMainListResponseDto> result = sut.getAllRecipeList(pageable, sortType);
        System.out.println(result.getContent());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
//        result.getContent().forEach(dto -> {
//            assertThat(dto.getSubCategoryList()).isNotEmpty();
//        });
    }

    @DisplayName("[happy] 유효한 레시피 ID로 상세 조회 시, 상세 정보와 북마크 여부가 반환된다.")
    @Test
    void getRecipeDetailViewWithValidRecipeIdTest() {
        // Given
        Recipe domain = Recipe.of(1L, 1L);
        Long memberId = 1L; // 예시 유저 ID
        TestJwtConfig.setupMockJwt(memberId, "진안");

        // When
        Recipe result = sut.getRecipeDetailView(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(domain.getId());
        assertThat(result.getRecipeName()).isNotNull();
        assertThat(result.getNickname()).isNotNull();
        assertThat(result.getRecipeDesc()).isNotNull();
        assertThat(result.isBookmarked()).isFalse(); // 북마크 여부 확인
    }

    @DisplayName("[bad] 존재하지 않는 레시피 ID로 상세 조회 시, 예외가 발생한다.")
    @Test
    void getRecipeDetailViewException1() {
        // Given
        Long memberId = 1L; // 예시 유저 ID
        Recipe domain = Recipe.of(9999L, memberId);
        TestJwtConfig.setupMockJwt(memberId, "진안");


        // When & Then
        assertThatThrownBy(() -> sut.getRecipeDetailView(domain))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    //todo: 테스트 통과시키기
//    @DisplayName("[bad] 레시피 ID는 존재하지만 멤버 ID가 존재하지 잘못 상태로 상세 조회 시, 예외가 발생한다.")
//    @Test
//    void getRecipeDetailViewException2() {
//        // Given
//        Long memberId = 3333L; // 예시 유저 ID
//        Recipe domain = Recipe.of(1L, memberId);
//        TestJwtConfig.setupMockJwt(memberId, "진안");
//
//
//        // When & Then
//        assertThatThrownBy(() -> sut.getRecipeDetailView(domain))
//                .isInstanceOf(RecipeApplicationException.class)
//                .hasMessageContaining("레시피가 존재하지 않습니다.")
//                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
//    }

    @DisplayName("[happy] 유효한 레시피 ID로 상세 조회 시 데이터를 잘 받아온다.")
    @Test
    void getRecipeDetailViewWithSubCategoryTest() {
        // Given
        Long memberId = 1L; // 예시 유저 ID
        Recipe domain = Recipe.of(1L, memberId);
        TestJwtConfig.setupMockJwt(memberId, "진아");

        // When
        Recipe result = sut.getRecipeDetailView(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(domain.getId());
    }

    @DisplayName("[happy] 레시피를 업데이트 하면 업데이트된 레시피의 id값이 반환된다.")
    @Test
    void updateRecipeHappy() {
        //given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        NutritionalInfoEntity savedNutritionalInfoEntity = nutritionalInfoRepository.findById(1L).orElseThrow();

        NutritionalInfo savingNutritionInfo = createNutritionalInfoEntity(savedNutritionalInfoEntity.getId(), 50, 50, 50, 50, 50);
        Recipe recipe = createRecipeDomainWithId(savedRecipeEntity.getId(), savingNutritionInfo, 1L, List.of());

        //when
        Long updateRecipeId = sut.updateRecipe(recipe);
        entityManager.flush();
        entityManager.clear();

        //then
        RecipeEntity updatedEntity = recipeRepository.findById(updateRecipeId).orElseThrow();
        Assertions.assertThat(updatedEntity.getRecipeName()).isEqualTo("수정할 이름");
        Assertions.assertThat(updatedEntity.getRecipeDesc()).isEqualTo("수정할 내용");
    }

    @Test
    @DisplayName("[bad] 존재하지 않는 레시피 ID로 업데이트를 시도하면 예외가 발생한다.")
    void updateRecipeException1() {
        //given
        NutritionalInfo savingNutritionInfo = createNutritionalInfoEntity(1L, 50, 50, 50, 50, 50);
        Recipe recipe = createRecipeDomainWithId(100L, savingNutritionInfo, 1L, List.of());

        //when-then
        Assertions.assertThatThrownBy(() -> sut.updateRecipe(recipe))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    @DisplayName("[happy] 주어진 영양소 정보가 올바르게 업데이트되면 데이터베이스에 반영된다.")
    @Test
    void updateNutritionalInfoHappy() {
        //given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        NutritionalInfoEntity savedNutritionalInfoEntity = nutritionalInfoRepository.findById(1L).orElseThrow();

        NutritionalInfo savingNutritionInfo = createNutritionalInfoEntity(savedNutritionalInfoEntity.getId(), 50, 50, 50, 50, 50);
        Recipe recipe = createRecipeDomainWithId(savedRecipeEntity.getId(), savingNutritionInfo, 1L, List.of());

        //when
        // 이렇게하면 트랜잭션이 안끝나서 계속 이전값인 10을 반환함....
        sut.updateNutritionalInfo(recipe);
        entityManager.flush();
        entityManager.clear();

        //then
        NutritionalInfoEntity updatedNutritionInfoEntity = nutritionalInfoRepository.findById(savedNutritionalInfoEntity.getId()).orElseThrow();
        Assertions.assertThat(updatedNutritionInfoEntity.getCarbohydrates()).isEqualTo(50);
    }

    @Test
    @DisplayName("[bad] 존재하지 않는 영양소 ID로 업데이트를 시도하면 예외가 발생한다.")
    void updateNutritionalInfoException() {
        //given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        NutritionalInfo savingNutritionInfo = createNutritionalInfoEntity(3L, 50, 50, 50, 50, 50);
        Recipe recipe = createRecipeDomainWithId(savedRecipeEntity.getId(), savingNutritionInfo, 1L, List.of());

        //when-then
        Assertions.assertThatThrownBy(() -> sut.updateNutritionalInfo(recipe))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("업데이트 하려는 영양소 정보가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NUTRITIONAL_INFO_NOT_FOUND);
    }

    @DisplayName("[happy] 카테고리 매핑 정보가 올바르게 업데이트되면 데이터베이스에 반영된다.")
    @Test
    void updateCategoryMappingHappy() {
        //given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        NutritionalInfo savingNutritionInfo = createNutritionalInfoEntity(3L, 50, 50, 50, 50, 50);

        // 매핑 테이블에는 recipe_id, subCategory_id만 존재: 테스트에서 1번 레시피는 서브 카테고리 5,6,7과 기본적으로 매핑되어 있다.
        List<SubCategory> subCategoryList = List.of(SubCategory.of(1L), SubCategory.of(2L), SubCategory.of(3L));
        Recipe recipe = createRecipeDomainWithId(savedRecipeEntity.getId(), savingNutritionInfo, 1L, subCategoryList);

        //when
        sut.updateCategoryMapping(recipe);
        entityManager.flush();
        entityManager.clear();

        //then
        List<RecipeCategoryMapEntity> byRecipeEntityId = recipeCategoryMapRepository.findByRecipeEntityId(recipe.getId());
        List<Long> idList = byRecipeEntityId.stream()
                .map(entity -> entity.getSubCategoryEntity().getId())
                .toList();

        List<Long> insertId = Arrays.asList(1L, 2L, 3L);

        Assertions.assertThat(idList).isNotEmpty();
        Assertions.assertThat(idList).isEqualTo(insertId);
    }

    @DisplayName("[bad] 존재하지 않는 레시피 ID로 카테고리 매핑 업데이트를 시도하면 예외가 발생한다.")
    @Test
    void updateCategoryMappingException1() {
        //given
        NutritionalInfo savingNutritionInfo = createNutritionalInfoEntity(3L, 50, 50, 50, 50, 50);

        // 매핑 테이블에는 recipe_id, subCategory_id만 존재: 테스트에서 1번 레시피는 서브 카테고리 5,6,7과 기본적으로 매핑되어 있다.
        List<SubCategory> subCategoryList = List.of(SubCategory.of(1L), SubCategory.of(2L), SubCategory.of(3L));
        Recipe recipe = createRecipeDomainWithId(20L, savingNutritionInfo, 1L, subCategoryList);


        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateCategoryMapping(recipe))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    @DisplayName("[bad] 존재하지 않는 서브 카테고리 ID로 업데이트를 시도하면 예외가 발생한다.")
    @Test
    void updateCategoryMappingException2() {
        //given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        NutritionalInfo savingNutritionInfo = createNutritionalInfoEntity(3L, 50, 50, 50, 50, 50);

        // 매핑 테이블에는 recipe_id, subCategory_id만 존재: 테스트에서 1번 레시피는 서브 카테고리 5,6,7과 기본적으로 매핑되어 있다.
        List<SubCategory> subCategoryList = List.of(SubCategory.of(200L), SubCategory.of(207L), SubCategory.of(3L));
        Recipe recipe = createRecipeDomainWithId(savedRecipeEntity.getId(), savingNutritionInfo, 1L, subCategoryList);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateCategoryMapping(recipe))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("존재하지 않는 서브 카테고리입니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SUB_CATEGORY_NOT_EXIST);
    }


    @DisplayName("[happy] 이미지를 삭제하면 테이블 내부의 del_yn 컬럼이 모두 'Y'로 변경된다.")
    @Test
    void deleteRecipeFilesByRecipeIdHappy() {
        //given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        Recipe domain = Recipe. of(savedRecipeEntity.getId(), savedRecipeEntity.getMemberId());

        RecipeFileEntity recipeFileEntity = RecipeFileEntity.of(savedRecipeEntity, 1, "/", "url", "nm", "nm2", "jpg", 100, "N");
        RecipeFileEntity savedFileEntity = recipeFileRepository.save(recipeFileEntity);
        entityManager.flush();
        entityManager.clear();

        List<Integer> orderList = new ArrayList<>();
        orderList.add(1);

        //when
        Long updatedCount = sut.softDeleteRecipeFile(domain, orderList);
        entityManager.flush();
        entityManager.clear();

        //then
        RecipeFileEntity result = recipeFileRepository.findById(savedFileEntity.getId()).orElseThrow();
        Assertions.assertThat(result.getDelYn()).isEqualTo("Y");
    }

    @DisplayName("[happy] 레시피를 삭제하면 soft delete가 적용되어 del_yn이 'Y'로 변경된다.")
    @Test
    void softDeleteRecipeByRecipeIdHappy() {
        //given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        Long recipeId = savedRecipeEntity.getId();
        Recipe recipe = Recipe.of(recipeId);

        //when
        Long deleteCount = sut.softDeleteByRecipeId(recipe);
        entityManager.flush();
        entityManager.clear();

        //then
        RecipeEntity result = recipeRepository.findById(savedRecipeEntity.getId()).orElseThrow();
        // del_yn은 Y인 조건으로 검색하면 된다.
        Assertions.assertThat(result.getDelYn()).isEqualTo("Y");
        Assertions.assertThat(deleteCount).isEqualTo(1);
    }


    private Recipe createRecipeDomain(long memberId, List<SubCategory> subCategory) {
        return Recipe.of(
                memberId,
                "레시피",
                "레시피 설명",
                20,
                "닭",
                "#진안",
                NutritionalInfo.of(10, 10, 10, 10, 10),
                subCategory,
                "진안",
                "N",
                0L,
                0,
                false,
                Collections.emptyList()
        );
    }

    public Recipe createRecipeDomainWithId(Long recipeId, NutritionalInfo savingNutritionInfo, long memberId, List<SubCategory> subCategory) {
        return Recipe.of(
                recipeId,
                memberId,
                "수정할 이름",
                "수정할 내용",
                2000,
                "수정1, 수정2, 수정3",
                "수정이다, 수정맞아, 수정이네",
                savingNutritionInfo,
                subCategory,
                "진안",
                "N",
                0L,
                0,
                false,
                Collections.emptyList()
        );
    }

    public NutritionalInfo createNutritionalInfoEntity(long id, int carbohydrates, int protein, int fat, int vitamins, int minerals) {
        NutritionalInfoDto dto = NutritionalInfoDto.of(id, carbohydrates, protein, fat, vitamins, minerals);
        return NutritionalInfo.of(
                dto.getId(),
                dto.getCarbohydrates(),
                dto.getProtein(),
                dto.getFat(),
                dto.getVitamins(),
                dto.getMinerals()
        );
    }


}
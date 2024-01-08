package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeCategoryMapEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

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


    @DisplayName("[happy] 유저가 닉네임을 변경하면 레시피 엔티티 내부의 유저 닉네임도 변경된다.")
    @Transactional
    @Test
    public void updateRecipesNicknames() {
        //given
        NicknameDto nicknameDto = NicknameDto.of(1L, "changedNickname");

        //when
        Long updatedCount = sut.updateRecipesNicknames(nicknameDto);

        //then
        String nickname = recipeRepository.findById(nicknameDto.memberId()).get().getNickname();
        assertThat(nickname).isEqualTo(nicknameDto.nickname());
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }

    @DisplayName("[bad] 존재하지 않는 유저(memberId)가 닉네임 변경을 시도하면 예외가 발생한다.")
    @Test
    void updateRecipesNicknamesFail() {
        //given
        NicknameDto nicknameDto = NicknameDto.of(100L, "NotValidNickname");

        //when & then
        assertThatThrownBy(() -> sut.updateRecipesNicknames(nicknameDto))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("유저를 찾을 수 없습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

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
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_VALID);
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
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_VALID);
    }

    @DisplayName("[bad] 존재하지 않는 카테고리 저장을 시도하면 예외가 발생한다.")
    @Test
    void createRecipeCategoryMapException() {
        //given
        Recipe domain = createRecipeDomain(1L, List.of(SubCategory.of(50L)));
        Long recipeId = sut.createRecipe(domain);

        //when & then
        assertThatThrownBy(() -> sut.createRecipeCategoryMap(domain, recipeId))
                .hasMessageContaining("존재하지 않는 카테고리입니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
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
        result.getContent().forEach(dto -> {
            assertThat(dto.getSubCategoryList()).isNotEmpty();
        });
    }

    @DisplayName("[happy] 유효한 레시피 ID로 상세 조회 시, 상세 정보와 북마크 여부가 반환된다.")
    @Test
    void getRecipeDetailViewWithValidRecipeIdTest() {
        // Given
        Long validRecipeId = 1L; // 존재하는 레시피 ID
        Long memberId = 1L; // 예시 유저 ID
        TestJwtConfig.setupMockJwt(memberId, "진안");

        // When
        RecipeDetailViewDto result = sut.getRecipeDetailView(validRecipeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(validRecipeId);
        assertThat(result.getRecipeName()).isNotNull();
        assertThat(result.getNickname()).isNotNull();
        assertThat(result.getRecipeDesc()).isNotNull();
        assertThat(result.isBookmarked()).isFalse(); // 북마크 여부 확인
        assertThat(result.getSubCategoryList()).isNotEmpty(); // 서브 카테고리 목록 확인
    }

    @DisplayName("[bad] 존재하지 않는 레시피 ID로 상세 조회 시, 예외가 발생한다.")
    @Test
    void getRecipeDetailViewWithInvalidRecipeIdTest() {
        // Given
        Long invalidRecipeId = 9999L; // 존재하지 않는 레시피 ID
        Long memberId = 1L; // 예시 유저 ID
        TestJwtConfig.setupMockJwt(memberId, "진안");


        // When & Then
        assertThatThrownBy(() -> sut.getRecipeDetailView(invalidRecipeId))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    @DisplayName("[happy] 유효한 레시피 ID로 상세 조회 시, 올바른 서브 카테고리 정보가 포함된다.")
    @Test
    void getRecipeDetailViewWithSubCategoryTest() {
        // Given
        Long validRecipeId = 1L; // 존재하는 레시피 ID
        Long memberId = 1L; // 예시 유저 ID
        TestJwtConfig.setupMockJwt(memberId, "진안");


        // When
        RecipeDetailViewDto result = sut.getRecipeDetailView(validRecipeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(validRecipeId);
        // 서브 카테고리 정보 확인
        assertThat(result.getSubCategoryList()).isNotEmpty();
        result.getSubCategoryList().forEach(subCategoryName -> {
            assertThat(subCategoryName).isNotNull().isNotBlank();
        });
    }

    // todo: 레시피 업데이트
    @DisplayName("[happy] 레시피를 업데이트 하면 업데이트된 레시피의 id값이 반환된다.")
    @Test
    void test() {
        //given

        //when

        //then

    }

    // todo: 영양소 업데이트
    @DisplayName("[happy] 주어진 영양소 정보가 올바르게 업데이트되면 데이터베이스에 반영된다.")
    @Test
    void updateNutritionalInfoHappy() {
        //given
        RecipeEntity savedRecipeEntity = recipeRepository.findById(1L).orElseThrow();
        NutritionalInfoEntity savedNutritionalInfoEntity = nutritionalInfoRepository.findById(1L).orElseThrow();
        Recipe recipe = createRecipeDomainWithId(savedRecipeEntity.getId(), savedNutritionalInfoEntity, 1L, List.of());

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
    @DisplayName("[bad] 잘못된 데이터로 영양소를 업데이트하면, 적절한 예외가 발생한다")
    void test3() {
        //given

        //when

        //then

    }

    @Test
    @DisplayName("[bad] 존재하지 않는 영양소 ID로 업데이트를 시도하면 예외가 발생한다.")
    void test4() {
        //given

        //when

        //then

    }



    // todo: 카테고리 매핑 업데이트










    // todo: 레시피에 연관된 파일 모두 삭제








    private RecipeEntity createRecipeEntity() {
        return RecipeEntity.of(
                1L,
                "레시피",
                "레시피 설명",
                20,
                "김치, 감자",
                "#고구마",
                "진안",
                "N"
        );
    }

    private NutritionalInfoEntity createNutritionalInfoEntity(RecipeEntity recipeEntity, int carbohydrates, int protein, int fat, int vitamins, int minerals) {
        return NutritionalInfoEntity.of(
                carbohydrates,
                protein,
                fat,
                vitamins,
                minerals,
                recipeEntity
        );
    }

    public NutritionalInfo getNutritionalInfoFromEntity(NutritionalInfoEntity updateEntity) {
        return NutritionalInfo.of(updateEntity.getCarbohydrates(), updateEntity.getProtein(), updateEntity.getFat(), updateEntity.getVitamins(), updateEntity.getMinerals());
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
                "N"
        );
    }

    public Recipe createRecipeDomainWithId(Long recipeId, NutritionalInfoEntity savedNutritionalInfoEntity, long memberId, List<SubCategory> subCategory) {
        NutritionalInfo nutritionalInfo = NutritionalInfo.of(savedNutritionalInfoEntity.getId(), 50, 50, 50, 50, 50);
        return Recipe.of(
                recipeId,
                memberId,
                "레시피",
                "레시피 설명",
                20,
                "닭",
                "#진안",
                nutritionalInfo,
                subCategory,
                "진안",
                "N"
        );
    }


}
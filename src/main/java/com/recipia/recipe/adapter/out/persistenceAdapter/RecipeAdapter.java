package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.application.port.out.BookmarkPort;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.SubCategory;
import com.recipia.recipe.domain.converter.RecipeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter 클래스는 port 인터페이스를 구현한다.
 * port에 요청이 들어가면 port의 메서드를 모두 구현한 이 adapter가 호출되어 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeAdapter implements RecipePort {

    private final RecipeConverter converter;
    private final RecipeQueryRepository querydslRepository;
    private final RecipeRepository recipeRepository;
    private final NutritionalInfoRepository nutritionalInfoRepository;
    private final SubCategoryEntityRepository subCategoryEntityRepository;
    private final RecipeCategoryMapRepository recipeCategoryMapRepository;


    /**
     * memberId로 유저가 작성한 모든 레시피를 조회한 다음 그 레시피 엔티티가 가진 유저의 닉네임 컬럼을 변경
     */
    @Override
    public Long updateRecipesNicknames(NicknameDto nicknameDto) {
        long updateCount = querydslRepository.updateRecipesNicknames(nicknameDto)
                .orElseThrow(() -> new RecipeApplicationException(ErrorCode.DB_ERROR));

        // 만약 업데이트 개수가0이면 memberId가 존재하지 않기 때문
        if (updateCount == 0) {
            throw new RecipeApplicationException(ErrorCode.USER_NOT_FOUND);
        }

        log.info("Updated {} recipe(s) with new nickname '{}' for memberId {}", updateCount, nicknameDto.nickname(), nicknameDto.memberId());
        return updateCount;
    }

    /**
     * 레시피를 저장하는 메서드
     * 저장에 성공하면 레시피 엔티티의 id값을 반환한다.
     */
    @Override
    public Long createRecipe(Recipe recipe) {
        // 여기서 memberId는 항상 유효하다
        RecipeEntity recipeEntity = converter.domainToRecipeEntity(recipe);
        recipeRepository.save(recipeEntity);
        return recipeEntity.getId();
    }

    /**
     * 영양소를 저장하는 메서드
     * 저장에 성공하면 영양소 엔티티의 id값을 반환한다.
     */
    @Override
    public Long createNutritionalInfo(Recipe recipe, Long savedRecipeId) {
        //1. 도메인에 새롭게 저장하려는 레시피의 id추가
        recipe.setId(savedRecipeId);
        //2. 컨버터를 통해 도메인을 영양소 엔티티로 변환
        NutritionalInfoEntity nutritionalInfoEntity = converter.domainToNutritionalInfoEntity(recipe);
        //3. 저장을 한다.
        return nutritionalInfoRepository.save(nutritionalInfoEntity).getId();
    }

    /**
     * 카테고리를 저장하는 메서드
     * 카테고리 Map 테이블에 저장한다.
     */
    @Override
    public void createRecipeCategoryMap(Recipe recipe, Long savedRecipeId) {
        recipe.setId(savedRecipeId);
        List<SubCategory> subCategoryList = validSubCategory(recipe);

        subCategoryList.stream()
                .map(subCategory -> converter.domainToRecipeCategoryMapEntity(recipe, subCategory))
                .forEach(recipeCategoryMapRepository::save);
    }

    /**
     * 서브 카테고리를 검증한다.
     */
    private List<SubCategory> validSubCategory(Recipe recipe) {
        List<SubCategory> subCategoryList = recipe.getSubCategory();

        // subCategoryList가 null인 경우 커스텀 예외를 던짐
        if (subCategoryList == null || subCategoryList.isEmpty()) {
            throw new RecipeApplicationException(ErrorCode.CATEGORY_NOT_VALID);
        }

        subCategoryList.forEach(subCategory -> {
            // subCategory가 데이터베이스에 존재하는지 확인
            boolean exists = subCategoryEntityRepository.findById(subCategory.getId()).isPresent();
            if (!exists) {
                // 존재하지 않는 경우 예외 발생
                throw new RecipeApplicationException(ErrorCode.CATEGORY_NOT_FOUND);
            }
        });
        return subCategoryList;
    }


}
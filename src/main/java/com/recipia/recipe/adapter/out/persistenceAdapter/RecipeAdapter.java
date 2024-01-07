package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.querydsl.core.Tuple;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.*;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import com.recipia.recipe.domain.SubCategory;
import com.recipia.recipe.domain.converter.RecipeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adapter 클래스는 port 인터페이스를 구현한다.
 * port에 요청이 들어가면 port의 메서드를 모두 구현한 이 adapter가 호출되어 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeAdapter implements RecipePort {

    private final SecurityUtil securityUtil;
    private final RecipeConverter converter;
    private final RecipeQueryRepository querydslRepository;
    private final RecipeRepository recipeRepository;
    private final NutritionalInfoRepository nutritionalInfoRepository;
    private final SubCategoryEntityRepository subCategoryEntityRepository;
    private final RecipeCategoryMapRepository recipeCategoryMapRepository;
    private final RecipeFileRepository recipeFileRepository;


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
     * 전체 레시피를 조회하는 메서드
     * querydsl을 사용해서 데이터를 조회한다.
     */
    @Override
    public Page<RecipeMainListResponseDto> getAllRecipeList(Pageable pageable, String sortType) {
        // 1. 로그인 된 유저 정보가 있어야 북마크 여부 확인이 가능하여 security에서 id를 받아서 사용한다.
        Long currentMemberId = securityUtil.getCurrentMemberId();
        Page<RecipeMainListResponseDto> recipeList = querydslRepository.getAllRecipeList(currentMemberId, pageable, sortType);

        // 2. 받아온 데이터의 모든 recipeId값을 추출한다.
        List<Long> selectedRecipeIdList = recipeList.getContent()
                .stream()
                .map(RecipeMainListResponseDto::getId)
                .toList();

        // 3. recipeId로 관련된 서브 카테고리를 받아온다.
        List<Tuple> subCategoryNameResults = querydslRepository.findSubCategoriesForRecipe(selectedRecipeIdList);
        Map<Long, List<String>> subCategoryNameMap = getSubCategoryNameMap(subCategoryNameResults);

        // 4. 결과값 dto에 서브 카테고리를 추가한다.
        recipeList.getContent().forEach(dto -> dto.setSubCategoryList(subCategoryNameMap.get(dto.getId())));
        return recipeList;
    }

    /**
     * 한개의 레시피를 조회하는 레시피
     */
    @Override
    public RecipeDetailViewDto getRecipeDetailView(Long recipeId) {
        // 1. 로그인 된 유저 정보가 있어야 북마크 여부 확인이 가능하여 security에서 id를 받아서 사용한다.
        Long currentMemberId = securityUtil.getCurrentMemberId();
        RecipeDetailViewDto recipeDetailViewDto = querydslRepository.getRecipeDetailView(recipeId, currentMemberId)
                .orElseThrow(() -> new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND));

        // 2. recipeId로 관련된 서브 카테고리를 받아온다.
        List<Tuple> subCategoriesForRecipe = querydslRepository.findSubCategoriesForRecipe(recipeDetailViewDto.getId());
        List<String> sunCategoryNames = getSubCategoryNameMap(subCategoriesForRecipe).get(recipeDetailViewDto.getId());
        recipeDetailViewDto.setSubCategoryList(sunCategoryNames);
        return recipeDetailViewDto;
    }

    /**
     * s3에 저장된 파일 정보를 rdb에 저장한다.
     * 서비스 레이어에서 이미 s3에 이미지는 업로드가 완료되고 이곳에서는 그 url정보를 함께 담아서 rdb에 저장한다.
     */
    @Override
    public List<Long> saveRecipeFile(List<RecipeFile> recipeFile) {
        try {
            // 1. 도메인을 엔티티로 변환한다.
            List<RecipeFileEntity> recipeFileEntities = recipeFile.stream()
                    .map(converter::domainToRecipeFileEntity)
                    .collect(Collectors.toList());

            // 2. 모든 레시피 파일 엔티티를 RDB에 저장하고 저장된 엔티티를 받아서 id값 리스트로 변환해서 반환한다.
            List<RecipeFileEntity> savedEntities = recipeFileRepository.saveAll(recipeFileEntities);
            return savedEntities.stream()
                    .map(RecipeFileEntity::getId)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            // 로그 기록
            log.error("[Error] saving recipe files", e);
            // 사용자 정의 예외 또는 특정 행동 수행
            throw new RecipeApplicationException(ErrorCode.RECIPE_FILE_SAVE_ERROR);
        }
    }

    /**
     * 레시피를 업데이트 한다.
     */
    @Override
    public Long updateRecipe(Recipe recipe) {
        RecipeEntity recipeEntity = converter.domainToRecipeEntity(recipe);
        return querydslRepository.updateRecipe(recipeEntity);
    }

    /**
     * 영양소를 업데이트 한다.
     */
    @Override
    public void updateNutritionalInfo(Recipe recipe) {
        NutritionalInfoEntity nutritionalInfoEntity = converter.domainToNutritionalInfoEntity(recipe);
        querydslRepository.updateNutritionalInfo(nutritionalInfoEntity);
    }

    /**
     * 카테고리 매핑을 업데이트 한다.
     * 정책상 최대3개의 카테고리만 업데이트하기 때문에 성능 문제는 없을듯 하여 기존의 카테고리를 지우고 새로운 카테고리를 저장한다.
     */
    @Override
    public void updateCategoryMapping(Recipe recipe) {
        // 기존 카테고리 매핑 삭제
        recipeCategoryMapRepository.deleteByRecipeEntityId(recipe.getId());

        // 새로운 카테고리 리스트 가져오기
        List<SubCategory> newSubCategoryList = validSubCategory(recipe);
        RecipeEntity recipeEntity = converter.domainToRecipeEntity(recipe);

        // 새 카테고리 매핑 저장
        newSubCategoryList.forEach(subCategory ->
                recipeCategoryMapRepository.save(RecipeCategoryMapEntity.of(recipeEntity, SubCategoryEntity.of(subCategory.getId())
                )));
    }

    /**
     * 레시피와 연관된 파일을 모두 지운다.
     */
    @Override
    public void deleteRecipeFilesByRecipeId(Long updatedRecipeId) {
        recipeFileRepository.deleteByRecipeEntityId(updatedRecipeId);
    }


    /**
     * 서브 카테고리를 Map<Long, List<String>> 형태로 받아온다.
     * Long에는 recipeId가 List<String>에는 서브 카테고리 이름들이 들어가 있다.
     */
    public Map<Long, List<String>> getSubCategoryNameMap(List<Tuple> subCategoryNameResults) {
        return subCategoryNameResults.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(0, Long.class),
                        Collectors.mapping(tuple -> tuple.get(1, String.class), Collectors.toList())
                ));
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
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
 * 외부(DB)와의 연결을 관리한다.
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
     * [UPDATE] - 레시피 테이블의 닉네임 컬럼 변경
     * MSA 프로젝트라서 레시피 테이블에는 [유저의 닉네임] 컬럼이 존재한다.
     * 유저가 닉네임을 변경하면 SNS로 이벤트가 발행되고 SQSListener가 동작해서 이 메서드가 호출된다.
     * 여기서는 memberId로 유저가 작성한 모든 레시피를 조회한 다음 그 레시피 엔티티가 가진 유저의 닉네임 컬럼을 변경한다.
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
     * [CREATE] - 레시피 저장
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
     * [CREATE] - 영양소 저장
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
     * [CREATE] - 카테고리 저장
     * 카테고리 Map 테이블에 서브 카테고리 정보를 저장한다.
     */
    @Override
    public void createRecipeCategoryMap(Recipe recipe, Long savedRecipeId) {
        recipe.setId(savedRecipeId);
        List<SubCategory> subCategoryList = validSubCategoryNotExist(recipe);

        subCategoryList.stream()
                .map(subCategory -> converter.domainToRecipeCategoryMapEntity(recipe, subCategory))
                .forEach(recipeCategoryMapRepository::save);
    }

    /**
     * [READ] - 레시피 목록 조회(전체)
     * querydsl을 사용해서 데이터를 조회를 최적화 했다. (목록, count)
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
     * [READ] - 단건의 레시피 상세조회
     * 유저가 작성한 레시피 정보를 상세조회한다.
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
     * [CREATE] - S3에 업로드된 파일(이미지) 정보 저장
     * 서비스에서 s3에 이미지 업로드가 완료된 후 호출되어 s3 object의 url정보를 rdb에 저장한다.
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
     * [UPDATE] - 레시피 업데이트
     * 레시피의 [이름, 내용(설명), 재료, 해시태그]를 모두 업데이트 한다.
     */
    @Override
    public Long updateRecipe(Recipe recipe) {
        // 1. 레시피 id를 통해 레시피의 존재여부를 파악하고 예외처리를 한다.
        validExistRecipeEntity(recipe);

        // 2. 도메인을 엔티티로 변환하고 레시피를 업데이트 한다.
        RecipeEntity recipeEntity = converter.domainToRecipeEntity(recipe);
        return querydslRepository.updateRecipe(recipeEntity);
    }

    /**
     * [UPDATE] - 영양소 업데이트
     * 영양소의 모든 필드를 업데이트 한다.
     */
    @Override
    public void updateNutritionalInfo(Recipe recipe) {
        // 1. 레시피 id를 통해 레시피의 존재여부를 파악하고 예외처리를 한다.
        validExistRecipeEntity(recipe);

        // 2. 업데이트하려는 영양소가 존재하는지 체크한다.
        Long nutritionalInfoId = recipe.getNutritionalInfo().getId();
        boolean exists = nutritionalInfoRepository.existsById(nutritionalInfoId);
        if (!exists) {
            throw new RecipeApplicationException(ErrorCode.NUTRITIONAL_INFO_NOT_FOUND);
        }

        // 3. 업데이트할 값을 엔티티로 변환하고 업데이트 한다.
        NutritionalInfoEntity updateNutritionalInfoEntity = converter.domainToNutritionalInfoEntityUpdate(recipe);
        querydslRepository.updateNutritionalInfo(updateNutritionalInfoEntity);
    }


    /**
     * [UPDATE] - 카레고리 매핑정보 업데이트
     * 기존의 카테고리를 지우고 새로운 카테고리를 저장한다.
     * 정책상 사용자는 최대3개의 카테고리만 선택할 수 있기 때문에 성능 문제는 없을듯 하여 선택한 방법이다.
     */
    @Override
    public void updateCategoryMapping(Recipe recipe) {

        // 1. 레시피 id를 통해 레시피의 존재여부를 파악하고 예외처리를 한다.
        validExistRecipeEntity(recipe);

        // 2. 매핑하려는 카테고리가 존재하는 서브 카테고리인지 검증하고 예외처리를 한다.
        List<SubCategory> subCategoryList = validSubCategoryNotExist(recipe);

        // 3. 기존 카테고리 매핑을 삭제한다.
        recipeCategoryMapRepository.deleteByRecipeEntityId(recipe.getId());

        // 3. 저장을 위해 새로운 카테고리 리스트를 한번 검증하고 다시 반환한다.
        RecipeEntity recipeEntity = converter.domainToRecipeEntity(recipe);

        // 4. 카테고리 매핑을 저장한다.
        subCategoryList.forEach(subCategory ->
                recipeCategoryMapRepository.save(RecipeCategoryMapEntity.of(recipeEntity, SubCategoryEntity.of(subCategory.getId())
                )));
    }

    /**
     * [DELETE] - 레시피에 연관된 파일 모두 삭제
     * S3에 이미지를 업로드할 때 날짜-UUID 이런식으로 저장되어서 RDB의 조건문으로 검색해서 update하기에는 무리가 있어 모두 삭제하고 다시 넣기로 결정
     */
    @Override
    public void deleteRecipeFilesByRecipeId(Long updatedRecipeId) {
        recipeFileRepository.deleteByRecipeEntityId(updatedRecipeId);
    }

    /**
     * [EXTRACT METHOD]
     * 카테고리 매핑을 추가/업데이트 할 때 검증하는 메서드
     */
    private List<SubCategory> validSubCategoryNotExist(Recipe recipe) {

        // 1. subCategoryList가 null인 경우 커스텀 예외를 던짐
        List<SubCategory> subCategoryList = recipe.getSubCategory();
        if (subCategoryList == null || subCategoryList.isEmpty()) {
            throw new RecipeApplicationException(ErrorCode.SUB_CATEGORY_IS_NULL_OR_EMPTY);
        }

        // 2. 서브 카테고리의 id값만 추출해서 List로 만든다.
        List<Long> subCategoryIdList = recipe.getSubCategory()
                .stream()
                .map(SubCategory::getId)
                .toList();

        // 3. 추출한 id를 사용해서 실제 존재하는 서브 카테고리인지 검증하고 각각 검증된 결과값 (bool)을 리스트로 받는다.
        List<Boolean> validResultList = subCategoryIdList.stream()
                .map(subCategoryEntityRepository::existsById)
                .toList();

        // 4. bool 리스트를 순회하며 만약 존재하지 않는 서브 카테고리가 하나라도 존재하면 예외를 던진다.
        validResultList.forEach(exist -> {
            if (!exist) {
                throw new RecipeApplicationException(ErrorCode.SUB_CATEGORY_NOT_EXIST);
            }
        });

        return subCategoryList;
    }

    /**
     * [Extract method] - getAllRecipeList, getRecipeDetailView 에서 사용
     * 서브 카테고리를 맵으로 변환하는 작업 (key: 레시피id, value: 서브 카테고리 리스트(최대3개)
     */
    public Map<Long, List<String>> getSubCategoryNameMap(List<Tuple> subCategoryNameResults) {
        return subCategoryNameResults.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(0, Long.class),
                        Collectors.mapping(tuple -> tuple.get(1, String.class), Collectors.toList())
                ));
    }

    /**
     * [EXTRACT METHOD]
     * 레시피 도메인에 설정된 id를 통해 존재하는 레시피인지 확인한다.
     */
    public void validExistRecipeEntity(Recipe recipe) {
        // 1. 연관된 레시피가 존재하는지 확인한다.
        boolean existEntity = recipeRepository.existsById(recipe.getId());

        // 2. 없다면 예외처리 한다.
        if (!existEntity) {
            throw new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND);
        }
    }


}
package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.querydsl.core.Tuple;
import com.recipia.recipe.adapter.in.web.dto.request.SubCategoryDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.*;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import com.recipia.recipe.domain.SubCategory;
import com.recipia.recipe.domain.converter.CategoryConverter;
import com.recipia.recipe.domain.converter.NutritionalInfoConverter;
import com.recipia.recipe.domain.converter.RecipeConverter;
import com.recipia.recipe.domain.converter.RecipeFileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 외부(DB)와의 연결을 관리한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeAdapter implements RecipePort {

    private final SecurityUtil securityUtil;

    private final RecipeConverter recipeConverter;
    private final NutritionalInfoConverter nutritionalInfoConverter;
    private final RecipeFileConverter recipeFileConverter;
    private final CategoryConverter categoryConverter;

    private final RecipeQueryRepository recipeQuerydslRepository;
    private final RecipeRepository recipeRepository;
    private final NutritionalInfoRepository nutritionalInfoRepository;
    private final SubCategoryEntityRepository subCategoryEntityRepository;
    private final RecipeCategoryMapRepository recipeCategoryMapRepository;
    private final RecipeFileRepository recipeFileRepository;


    /**
     * [CREATE] - 레시피 저장
     * 저장에 성공하면 레시피 엔티티의 id값을 반환한다.
     */
    @Override
    public Long createRecipe(Recipe recipe) {
        // 여기서 memberId는 항상 유효하다
        RecipeEntity recipeEntity = recipeConverter.domainToRecipeEntity(recipe);
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
        NutritionalInfoEntity nutritionalInfoEntity = nutritionalInfoConverter.domainToEntityCreate(recipe);
        //3. 저장을 한다.
        return nutritionalInfoRepository.save(nutritionalInfoEntity).getId();
    }

    /**
     * [CREATE] - 레시피랑 카테고리 맵핑 저장
     * 레시피와카테고리 Map 테이블에 레시피와 서브카테고리 정보를 저장한다.
     */
    @Override
    public void createRecipeCategoryMap(Recipe recipe, Long savedRecipeId) {
        recipe.setId(savedRecipeId);
        List<SubCategory> subCategoryList = validSubCategoryNotExist(recipe);

        subCategoryList.stream()
                .map(subCategory -> recipeConverter.domainToRecipeCategoryMapEntity(recipe, subCategory))
                .forEach(recipeCategoryMapRepository::save);
    }

    /**
     * [READ] - 레시피 목록 조회(전체)
     * querydsl을 사용해서 데이터를 조회를 최적화 했다. (목록, count)
     */
    @Override
    public Page<RecipeMainListResponseDto> getAllRecipeList(Pageable pageable, String sortType, List<Long> subCategoryList) {
        // 1. 로그인 된 유저 정보가 있어야 북마크 여부 확인이 가능하여 securityContext에서 id를 꺼내서 사용한다.
        Long currentMemberId = securityUtil.getCurrentMemberId();

        // 2. 조건에 맞는 모든 레시피 리스트를 가져온다.
        Page<RecipeMainListResponseDto> recipeResponseDtoList = recipeQuerydslRepository.getAllRecipeList(currentMemberId, pageable, sortType, subCategoryList);

        return recipeResponseDtoList;
    }

    /**
     * [READ] - 레시피 단건 상세조회
     * 레시피 정보를 상세조회한다.
     */
    @Override
    public Recipe getRecipeDetailView(Recipe domain) {
        // recipe 기본 정보를 가져온다.
        Recipe recipe = recipeQuerydslRepository.getRecipeDetailView(domain.getId(), domain.getMemberId()).orElseThrow(() -> new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND));

        return recipe;
    }

    /**
     * [READ] - 서브 카테고리 조회
     * recipeId를 통해 관련된 서브 카테고리들을 모두 받아서 그 id값들만 리스트 형태로 반환한다.
     */
    @Override
    public List<SubCategory> getSubCategories(Long recipeId) {

        // 1. 레시피 id로 관련된 서브 카테고리를 모두 찾는다.
        List<SubCategoryDto> subCategoriesForRecipe = recipeQuerydslRepository.findSubCategoryDtoListForRecipeId(recipeId);

        List<SubCategory> subCategoryDomainList = subCategoriesForRecipe.stream()
                .map(categoryConverter::dtoToDomain)
                .toList();

        return subCategoryDomainList;
    }

    /**
     * [READ] - 영양소 조회
     * recipeId를 통해 관련된 영양소 데이터를 받아온다.
     */
    @Override
    public NutritionalInfo getNutritionalInfo(Long recipeId) {

        NutritionalInfoEntity nutritionalInfoEntity = nutritionalInfoRepository.findByRecipe_Id(recipeId).orElseThrow(
                () -> new RecipeApplicationException(ErrorCode.NUTRITIONAL_INFO_NOT_FOUND)
        );

        return nutritionalInfoConverter.entityToNutritionalInfo(nutritionalInfoEntity);
    }


    /**
     * [READ] - 파일(이미지)정보 조회
     * recipeId를 통해 관련된 파일 정보 중 id, file order, file path를 조회한다.
     */
    @Override
    public List<RecipeFile> getRecipeFileList(Long recipeId) {
        List<RecipeFileEntity> resultEntityList = recipeFileRepository.findAllByRecipeId(recipeId);

        return resultEntityList.stream()
                .map(recipeFileConverter::entityToDomainIdOrderPath)
                .collect(Collectors.toList());
    }

    /**
     * [DELETE] - 레시피를 soft delete 방식으로 삭제한다.
     * 사용자로부터 받아온 recipeId로 삭제를 시도한다.
     * 사용자에게 복구기간을 1주일 주고 기간이 지날 시 배치를 통해 del_yn이 Y인 데이터를 진짜 삭제한다.
     */
    @Override
    public Long softDeleteByRecipeId(Recipe domain) {

        // 1. soft delete로 del_yn을 모두 "Y"로 변경한다.
        Long softDeletedRecipeCount = recipeQuerydslRepository
                .softDeleteRecipeByRecipeId(domain.getId());

        // 2. 로그를 남겨서 삭제 여부를 확인할 수 있도록 한다.
        log.info("recipeId : {}, softDeletedRecipeCount : {}", domain.getId(), softDeletedRecipeCount);
        return softDeletedRecipeCount;
    }

    /**
     * [READ] - 조건에 맞는 레시피를 가져와서 true/false로 반환한다.
     */
    @Override
    public boolean checkIsRecipeMineExist(Recipe domain) {

        // 1. 레시피id, 멤버id, del_yn을 조건으로 수정/삭제하려는 레시피의 주인이 맞는지 검색한다.
        Optional<RecipeEntity> optionalRecipeEntity = recipeRepository.
                findByIdAndMemberIdAndDelYn(domain.getId(), domain.getMemberId(), "N");

        // 2. 존재하면 true 없으면 false
        return optionalRecipeEntity.isPresent();
    }

    /**
     * [READ] - recipeId에 해당하는 레시피가 존재하면 true, 존재하지 않고 삭제된 레시피면 false를 반환한다.
     */
    @Override
    public boolean checkIsRecipeExist(Recipe domain) {
        // 1. 레시피id, del_yn을 조건으로 레시피를 가져온다
        Optional<RecipeEntity> optionalRecipeEntity = recipeRepository.
                findByIdAndDelYn(domain.getId(), "N");

        // 2. 존재하면 true 없으면 false
        return optionalRecipeEntity.isPresent();
    }

    /**
     * [READ] recipeId에 해당하는 파일의 최대 file order값을 반환한다.
     * 만약에 데이터가 없으면 0을, max값이 존재하면 max값을 반환한다.
     */
    @Override
    public Integer findMaxFileOrder(Long savedRecipeId) {
        return recipeFileRepository.findMaxFileOrderByRecipeEntity_Id(savedRecipeId).orElse(0);
    }

    /**
     * [DELETE] - 유저가 삭제를 요청한 파일은 모두 soft delete 한다.
     */
    @Override
    public Long softDeleteRecipeFile(Recipe domain, List<Integer> deleteFileOrder) {
        return recipeQuerydslRepository.softDeleteRecipeFile(domain, deleteFileOrder);
    }

    /**
     * [READ] memberId에 해당하는 회원이 작성한 레시피의 갯수를 반환한다.
     */
    @Override
    public Long getMyRecipeCount(Long memberId) {
        return recipeRepository.countByMemberIdAndDelYn(memberId, "N");
    }

    /**
     * [READ] 내가 작성한 recipe id 목록을 반환한다.
     */
    @Override
    public List<Long> getAllMyRecipeIds(Long memberId) {
        return recipeQuerydslRepository.findMyRecipeIds(memberId);
    }

    /**
     * [READ] 내가 작성한 recipe중에서 조회수가 높은 5개의 목록을 가져온다.
     */
    @Override
    public List<RecipeMainListResponseDto> getMyHighRecipeList(Long memberId, List<Long> myHighRecipeIds) {
        List<RecipeMainListResponseDto> resultList = recipeQuerydslRepository.getMyHighRecipeList(memberId, myHighRecipeIds);

        Map<Long, RecipeMainListResponseDto> recipesMap = resultList.stream()
                .collect(Collectors.toMap(RecipeMainListResponseDto::getId, dto -> dto));

        return myHighRecipeIds.stream()
                .map(recipesMap::get)
                .collect(Collectors.toList());
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
                    .map(recipeConverter::domainToRecipeFileEntity)
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
        RecipeEntity recipeEntity = recipeConverter.domainToRecipeEntity(recipe);
        return recipeQuerydslRepository.updateRecipe(recipeEntity);
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
        NutritionalInfoEntity updateNutritionalInfoEntity = nutritionalInfoConverter.domainToEntityUpdate(recipe.getNutritionalInfo());
        Long updatedNutritionalInfoId = recipeQuerydslRepository.updateNutritionalInfo(updateNutritionalInfoEntity);
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
        RecipeEntity recipeEntity = recipeConverter.domainToRecipeEntity(recipe);

        // 4. 카테고리 매핑을 저장한다.
        subCategoryList.forEach(subCategory ->
                recipeCategoryMapRepository.save(RecipeCategoryMapEntity.of(recipeEntity, SubCategoryEntity.of(subCategory.getId())
                )));
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
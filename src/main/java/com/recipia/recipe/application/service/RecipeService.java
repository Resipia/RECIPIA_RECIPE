package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.application.port.in.DeleteRecipeUseCase;
import com.recipia.recipe.application.port.in.ReadRecipeUseCase;
import com.recipia.recipe.application.port.in.UpdateRecipeUseCase;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.application.port.out.RedisPort;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import com.recipia.recipe.domain.SubCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService implements CreateRecipeUseCase, ReadRecipeUseCase, UpdateRecipeUseCase, DeleteRecipeUseCase {

    private final RecipePort recipePort;
    private final RedisPort redisPort;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageS3Service imageS3Service;

    /**
     * [CREATE] - 레시피 생성을 담당하는 메서드
     * 주관심사: 레시피 생성 (엔티티 저장)
     * 비관심사: 스프링 이벤트 발행 (재료, 해시태그 mongoDB에 저장)
     */
    @Transactional
    @Override
    public Long createRecipe(Recipe recipe, List<MultipartFile> files) {

        // 주관심사: 레시피 저장, 영양소 저장, 서브 카테고리 매핑, 파일 s3/rdb 저장
        Long savedRecipeId = recipePort.createRecipe(recipe);
        Long savedNutritionalInfoId = recipePort.createNutritionalInfo(recipe, savedRecipeId);
        recipePort.createRecipeCategoryMap(recipe, savedRecipeId);

        // 파일이 null이면 저장을 하지 않는다.
        if (!files.isEmpty()) {

            // 레시피 파일 저장을 위한 엔티티 생성 (이때 s3에는 이미 이미지가 업로드 완료되고 저장된 경로의 url을 받은 엔티티를 리스트로 생성)
            List<RecipeFile> recipeFileList = files
                    .stream()
                    .map(file -> imageS3Service.createRecipeFile(file, savedRecipeId))
                    .collect(Collectors.toList());

            // db에 레시피 파일(이미지)를 저장한다.
            List<Long> savedFileIdList = recipePort.saveRecipeFile(recipeFileList);

            if (savedFileIdList.isEmpty()) {
                throw new RecipeApplicationException(ErrorCode.RECIPE_FILE_SAVE_ERROR);
            }
        }

        // 비관심사: 스프링 이벤트 발행
        eventPublisher.publishEvent(new RecipeCreationEvent(recipe.getIngredient(), recipe.getHashtag()));

        return savedRecipeId;
    }

    /**
     * [READ] - 레시피 목록 전체 조회
     * 페이징을 위한 Pageable 객체를 여기서 조립해서 사용한다.
     * page=0과 size=10으로 Pageable 객체를 생성하면, 이는 '첫 번째 페이지에 10개의 항목을 보여달라'는 요청이다.
     * page=1과 size=10이면 '두 번째 페이지에 10개의 항목을 보여달라'는 요청이다.
     */
    @Override
    public PagingResponseDto<RecipeMainListResponseDto> getAllRecipeList(int page, int size, String sortType, List<Long> subCategoryList) {
        // 1. 정렬조건을 정한 뒤 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 2. 데이터를 받아온다.
        Page<RecipeMainListResponseDto> allRecipeList = recipePort.getAllRecipeList(pageable, sortType, subCategoryList);

        // 3. 받아온 데이터를 꺼내서 응답 dto에 값을 세팅해 준다.
        List<RecipeMainListResponseDto> content = allRecipeList.getContent();
        Long totalCount = allRecipeList.getTotalElements();
        return PagingResponseDto.of(content, totalCount);
    }

    /**
     * [READ] - 레시피 단건 상세조회
     * 서브 카테고리, 영양소, 이미지 정보는 각각 어댑터에 요청해서 받아온 후 조립한다.
     */
    public Recipe getRecipeDetailView(Recipe domain) {

        Long recipeId = domain.getId();
        // 1. 레시피의 기본적인 정보를 받아온다.
        Recipe recipe = recipePort.getRecipeDetailView(domain);

        // 2. 서브 카테고리 정보를 받아와서 도메인에 세팅한다.
        List<SubCategory> subCategories = recipePort.getSubCategories(recipeId);
        recipe.setSubCategory(subCategories);

        // 3. 영양소 정보를 받아와서 도메인에 세팅한다.
        NutritionalInfo nutritionalInfo = recipePort.getNutritionalInfo(recipeId);
        recipe.setNutritionalInfo(nutritionalInfo);

        // 4. 이미지(파일)을 받아와서 도메인에 세팅한다.
        List<RecipeFile> recipeFileList = recipePort.getRecipeFile(recipeId);
        recipe.setRecipeFileList(recipeFileList);

        // 조회수 증가 로직 호출 (트랜잭션에 포함되지 않음)
        redisPort.incrementViewCount(recipeId);

        return recipe;
    }

    /**
     * [UPDATE] - 레시피를 업데이트 한다.
     * 레시피 생성과 거의 동일하다. 다만 업데이트다 보니 기존의 데이터를 삭제하고 추가하는 방식을 주로 적용시켰다.
     */
    @Transactional
    @Override
    public void updateRecipe(Recipe domain, List<MultipartFile> files) {

        // 1. 레시피가 존재하고 업데이트하려는 유저가 작성한 것이 맞는지 체크한다. (예외처리)
        checkIsRecipeExistAndMine(domain);

        // 2. 레시피, 영양소, 카테고리 매핑 업데이트
        Long recipeId = recipePort.updateRecipe(domain);
        recipePort.updateNutritionalInfo(domain);
        recipePort.updateCategoryMapping(domain);

        // 3. 유저가 파일 삭제만 하고 업데이트를 했을수도 있으니 이 로직은 무조건 동작시킨다.
        recipePort.softDeleteRecipeFile(domain, domain.getDeleteFileOrder());

        // 4. 파일이 null이면 저장을 하지 않는다.
        if (!files.isEmpty()) {

            // 4-1. 레시피 파일 저장을 위한 엔티티 생성 (이때 s3에는 이미 이미지가 업로드 완료되고 저장된 경로의 url을 받은 엔티티를 리스트로 생성)
            List<RecipeFile> recipeFileList = files
                    .stream()
                    .map(file -> imageS3Service.createRecipeFile(file, recipeId))
                    .collect(Collectors.toList());

            // 4-2. rdb에 레시피 파일(이미지)을 저장
            List<Long> savedFileIdList = recipePort.saveRecipeFile(recipeFileList);

            // 4-3. 만약 저장 후 반환받은 id값이 없다면 예외처리
            if (savedFileIdList.isEmpty()) {
                throw new RecipeApplicationException(ErrorCode.RECIPE_FILE_SAVE_ERROR);
            }
        }

        // 5. 비관심사: 스프링 이벤트 발행 (몽고db: 재료, 해시태그 저장)
        eventPublisher.publishEvent(new RecipeCreationEvent(domain.getIngredient(), domain.getHashtag()));
    }

    /**
     * [DELETE] - 레시피를 삭제한다.
     * soft delete 방식을 적용하였으며 del_yn을 "Y" 로 변경한다.
     */
    @Transactional
    public Long deleteRecipeByRecipeId(Recipe domain) {

        // 1. 레시피가 존재하고 업데이트하려는 유저가 작성한 것이 맞는지 체크한다. (예외처리)
        checkIsRecipeExistAndMine(domain);

        return recipePort.softDeleteByRecipeId(domain);
    }

    /**
     * 레시피 도메인에서 recipeId, memberId, del_yn을 준비한다.
     */
    private void checkIsRecipeExistAndMine(Recipe recipe) {
        boolean isRecipeExist = recipePort.checkIsRecipeMineExist(recipe);
        if (!isRecipeExist) {
            throw new RecipeApplicationException(ErrorCode.RECIPE_IS_NOT_MINE);
        }
    }

}

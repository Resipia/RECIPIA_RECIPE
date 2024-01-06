package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.application.port.in.DeleteRecipeUseCase;
import com.recipia.recipe.application.port.in.ReadRecipeUseCase;
import com.recipia.recipe.application.port.in.UpdateRecipeUseCase;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import com.recipia.recipe.domain.converter.RecipeConverter;
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
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService implements CreateRecipeUseCase, ReadRecipeUseCase, UpdateRecipeUseCase, DeleteRecipeUseCase {

    private final RecipePort recipePort;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageS3Service imageS3Service;
    private final RecipeConverter converter;

    /**
     * 레시피 생성을 담당하는 메서드
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
            List<RecipeFile> recipeFileList = IntStream.range(0, files.size())
                    .mapToObj(fileOrder -> imageS3Service.createRecipeFileEntity(files.get(fileOrder), fileOrder, savedRecipeId))
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
     * 레시피 목록 전체 조회
     * 페이징을 위한 Pageable 객체를 여기서 조립해서 사용한다.
     * page=0과 size=10으로 Pageable 객체를 생성하면, 이는 '첫 번째 페이지에 10개의 항목을 보여달라'는 요청이다.
     * page=1과 size=10이면 '두 번째 페이지에 10개의 항목을 보여달라'는 요청이다.
     */
    @Override
    public PagingResponseDto<RecipeMainListResponseDto> getAllRecipeList(int page, int size, String sortType) {
        // 1. 정렬조건을 정한 뒤 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // todo: 여기에 securytiutils 넣을지말지

        // 2. 데이터를 받아온다.
        Page<RecipeMainListResponseDto> allRecipeList = recipePort.getAllRecipeList(pageable, sortType);

        // 3. 받아온 데이터를 꺼내서 응답 dto에 값을 세팅해 준다.
        List<RecipeMainListResponseDto> content = allRecipeList.getContent();
        Long totalCount = allRecipeList.getTotalElements();
        return PagingResponseDto.of(content, totalCount);
    }

    /**
     * 레시피 단건 조회
     */
    public RecipeDetailViewDto getRecipeDetailView(Long recipeId) {
        return recipePort.getRecipeDetailView(recipeId);
    }

}

package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateUpdateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.application.port.in.ReadRecipeUseCase;
import com.recipia.recipe.application.port.in.UpdateRecipeUseCase;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.converter.RecipeConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final CreateRecipeUseCase createRecipeUseCase;
    private final ReadRecipeUseCase readRecipeUseCase;
    private final UpdateRecipeUseCase updateRecipeUseCase;
    private final RecipeConverter converter;

    /**
     * 유저가 레시피 생성을 요청하는 컨트롤러
     */
    @PostMapping("/createRecipe")
    public ResponseEntity<ResponseDto<Long>> createRecipe(@Valid @ModelAttribute RecipeCreateUpdateRequestDto recipeCreateUpdateRequestDto) {

        // 1. dto에서 이미지 파일 리스트 추출
        List<MultipartFile> files = recipeCreateUpdateRequestDto.getFileList();

        // 2. dto to domain -> 이때 jwt가 없으면 MISSING_JWT 예외 발생, 유저가 없으면 USER_NOT_FOUND 예외 발생
        Recipe recipe = converter.recipeCreateDtoToDomain(recipeCreateUpdateRequestDto);

        // 3. 레시피 저장 실시
        Long savedRecipeId = createRecipeUseCase.createRecipe(recipe, files);

        return ResponseEntity.ok(ResponseDto.success(savedRecipeId));
    }

    /**
     * 메인 화면에서 전체 레시피를 조회
     * page와 size는 각각 '현재 페이지'와 '페이지 당 항목 수'를 의미한다.
     */
    @GetMapping("/getAllRecipeList")
    public ResponseEntity<PagingResponseDto<RecipeMainListResponseDto>> getAllRecipeList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortType", defaultValue = "new") String sortType
    ) {
        PagingResponseDto<RecipeMainListResponseDto> allRecipeList = readRecipeUseCase.getAllRecipeList(page, size, sortType);
        return ResponseEntity.ok(allRecipeList);
    }

    /**
     * 레시피 단건 조회
     */
    @GetMapping("/getRecipeDetail")
    public ResponseEntity<ResponseDto<RecipeDetailViewDto>> getRecipeDetailView(
            @RequestParam(value = "recipeId") Long recipeId
    ) {
        RecipeDetailViewDto recipeDetailViewDto = readRecipeUseCase.getRecipeDetailView(recipeId);
        return ResponseEntity.ok(ResponseDto.success(recipeDetailViewDto));
    }

    /**
     * 레시피 업데이트
     */
    @PutMapping("/updateRecipe")
    public ResponseEntity<ResponseDto<Void>> updateRecipe(@Valid @ModelAttribute RecipeCreateUpdateRequestDto recipeCreateUpdateRequestDto) {

        // 1. dto에서 이미지 파일 리스트 추출
        List<MultipartFile> files = recipeCreateUpdateRequestDto.getFileList();

        // 2. dto to domain -> 이때 jwt가 없으면 MISSING_JWT 예외 발생, 유저가 없으면 USER_NOT_FOUND 예외 발생
        Recipe recipe = converter.recipeCreateDtoToDomain(recipeCreateUpdateRequestDto);

        // 3. 서비스 메서드 호출하여 변경감지
        updateRecipeUseCase.updateRecipe(recipe, files);

        return ResponseEntity.ok(ResponseDto.success());
    }

}

package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.application.port.in.ReadRecipeUseCase;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.converter.RecipeConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final CreateRecipeUseCase createRecipeUseCase;
    private final ReadRecipeUseCase readRecipeUseCase;
    private final RecipeConverter converter;

    /**
     * 유저가 레시피 생성을 요청하는 컨트롤러
     */
    @PostMapping("/createRecipe")
    public ResponseEntity<ResponseDto<Long>> createRecipe(@Valid @RequestBody RecipeCreateRequestDto recipeCreateRequestDto) {

        // dto to domain -> 이때 jwt가 없으면 MISSING_JWT 예외 발생, 유저가 없으면 USER_NOT_FOUND 예외 발생
        Recipe recipe = converter.requestDtoToDomain(recipeCreateRequestDto);

        Long savedRecipeId = createRecipeUseCase.createRecipe(recipe);

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

}

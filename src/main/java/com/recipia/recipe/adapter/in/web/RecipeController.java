package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.converter.RecipeConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final CreateRecipeUseCase createRecipeUseCase;
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

}

package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/recipe")
@RestController
public class RecipeController {



    @PostMapping("/createRecipe")
    public ResponseEntity<ResponseDto<Void>> createRecipe(@Valid @RequestBody RecipeRequestDto recipeRequestDto) {

        return ResponseEntity.ok(ResponseDto.success());
    }

}

package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.RecipeRequestDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/recipe")
@RestController
public class RecipeController {



    @PostMapping("/createRecipe")
    public String createRecipe(@Valid @RequestBody RecipeRequestDto recipeRequestDto) {
        //1 .

        return "성공";
    }

}

package com.recipia.recipe.controller;

import com.recipia.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/test")
    public String pingTest() {
        return "recipe connect success";
    }


    @PostMapping("/nicknameChange")
    public String nicknameChange() {

        recipeService.nicknameChange();
        return "success";
    }

}

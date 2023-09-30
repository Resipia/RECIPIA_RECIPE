package com.recipia.recipe.controller;

import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @GetMapping("/hte")
    public String kafkatest (
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = (String) jwt.getClaims().get("username");

        return null;
    }

}

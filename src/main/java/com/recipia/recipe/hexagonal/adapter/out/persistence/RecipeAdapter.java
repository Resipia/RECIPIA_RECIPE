package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.application.port.out.RecipePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecipeAdapter implements RecipePort {

    private final RecipeRepository recipeRepository;



}
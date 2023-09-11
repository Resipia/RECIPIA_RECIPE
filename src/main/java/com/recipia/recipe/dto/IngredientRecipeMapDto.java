package com.recipia.recipe.dto;

import com.recipia.recipe.domain.IngredientRecipeMap;

public record IngredientRecipeMapDto(
        Long id,
        IngredientDto ingredient,
        CustomIngredientDto customIngredient,
        RecipeDto recipeDto
) {

    public static IngredientRecipeMapDto of(Long id, IngredientDto ingredient, CustomIngredientDto customIngredient, RecipeDto recipeDto) {
        return new IngredientRecipeMapDto(id, ingredient, customIngredient, recipeDto);
    }

    // entity -> dto 변환 메서드
    public static IngredientRecipeMapDto fromEntity(IngredientRecipeMap entity) {
        return of(
                entity.getId(),
                IngredientDto.fromEntity(entity.getIngredient()),
                CustomIngredientDto.fromEntity(entity.getCustomIngredient()),
                RecipeDto.fromEntity(entity.getRecipe())
        );
    }


}
package com.recipia.recipe.dto;

import com.recipia.recipe.hexagonal.adapter.out.persistence.IngredientRecipeMapEntity;

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
    public static IngredientRecipeMapDto fromEntity(IngredientRecipeMapEntity entity) {
        return of(
                entity.getId(),
                IngredientDto.fromEntity(entity.getIngredientEntity()),
                CustomIngredientDto.fromEntity(entity.getCustomIngredientEntity()),
                RecipeDto.fromEntity(entity.getRecipeEntity())
        );
    }


}
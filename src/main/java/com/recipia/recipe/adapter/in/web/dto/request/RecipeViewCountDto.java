package com.recipia.recipe.adapter.in.web.dto.request;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import lombok.Builder;
import lombok.Data;

@Data
public class RecipeViewCountDto {

    private Long id;
    private RecipeEntity recipeEntity;
    private Integer viewCount;

    @Builder
    private RecipeViewCountDto(Long id, RecipeEntity recipeEntity, Integer viewCount) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.viewCount = viewCount;
    }

    public static RecipeViewCountDto of(Long id, RecipeEntity recipeEntity, Integer viewCount) {
        return new RecipeViewCountDto(id, recipeEntity, viewCount);
    }

}

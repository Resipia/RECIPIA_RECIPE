package com.recipia.recipe.application.port.out;

import com.recipia.recipe.domain.RecipeLike;

import java.util.List;

public interface RecipeLikePort {

    Long saveLike(RecipeLike domain);

    void deleteRecipeLike(RecipeLike domain);

    Long deleteRecipeLikesInRecipeIds(List<Long> recipeIds);

    Long deleteLikeByMemberId(Long memberId);
}

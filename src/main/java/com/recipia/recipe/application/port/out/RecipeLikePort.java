package com.recipia.recipe.application.port.out;

import com.recipia.recipe.domain.RecipeLike;

public interface RecipeLikePort {

    Long saveLike(RecipeLike domain);

    void deleteRecipeLike(RecipeLike domain);
}

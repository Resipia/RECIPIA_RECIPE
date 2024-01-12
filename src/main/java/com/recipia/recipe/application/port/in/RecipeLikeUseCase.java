package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeLike;

public interface RecipeLikeUseCase {

    Long recipeLikeProcess(RecipeLike domain);

}

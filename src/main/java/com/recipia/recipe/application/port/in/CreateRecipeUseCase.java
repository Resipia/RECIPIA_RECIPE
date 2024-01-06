package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CreateRecipeUseCase {

    // 레시피 생성
    Long createRecipe(Recipe recipe, List<MultipartFile> files);

}

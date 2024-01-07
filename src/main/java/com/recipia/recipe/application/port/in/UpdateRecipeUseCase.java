package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UpdateRecipeUseCase {
    void updateRecipe(Recipe recipe, List<MultipartFile> files);
}

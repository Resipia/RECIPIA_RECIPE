package com.recipia.recipe.hexagonal.application.port.out;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
import com.recipia.recipe.hexagonal.domain.Recipe;

import java.util.List;

/**
 * adapter 클래스의 인터페이스
 */
public interface RecipePort {

    List<Recipe> findRecipeByMemberIdAndDelYn(Long memberId, String delYn);

}

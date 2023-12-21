package com.recipia.recipe.application.port.out;


import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;

/**
 * adapter 클래스의 인터페이스
 */
public interface RecipePort {

    void updateRecipesNicknames(NicknameDto nicknameDto);
}

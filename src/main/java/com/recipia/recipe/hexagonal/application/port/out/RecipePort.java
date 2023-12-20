package com.recipia.recipe.hexagonal.application.port.out;

import com.recipia.recipe.hexagonal.adapter.out.feign.dto.NicknameDto;

/**
 * adapter 클래스의 인터페이스
 */
public interface RecipePort {

    void updateRecipesNicknamesForMemberId(NicknameDto nicknameDto);
}

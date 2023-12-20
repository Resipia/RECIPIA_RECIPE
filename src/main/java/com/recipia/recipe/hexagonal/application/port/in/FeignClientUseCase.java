package com.recipia.recipe.hexagonal.application.port.in;

import com.recipia.recipe.hexagonal.adapter.out.feign.dto.NicknameDto;

public interface FeignClientUseCase {

    public void updateRecipesNicknamesForMemberId(NicknameDto nicknameDto);

}

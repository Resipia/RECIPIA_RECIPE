package com.recipia.recipe.application.port.in;


import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;

public interface FeignClientUseCase {

    Long updateRecipesNicknames(NicknameDto nicknameDto);

}

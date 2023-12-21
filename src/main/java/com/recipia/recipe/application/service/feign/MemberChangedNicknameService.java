package com.recipia.recipe.application.service.feign;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.application.port.in.FeignClientUseCase;
import com.recipia.recipe.application.port.out.RecipePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberChangedNicknameService implements FeignClientUseCase {

    private final RecipePort recipePort;

    /**
     * FeignClient로부터 받아온 변경된 닉네임을 모든 레시피 엔티티에 적용시키는 로직
     * nicknameDto는 Feign에서 받아온 응답이다.
     */
    @Override
    public void updateRecipesNicknames(NicknameDto nicknameDto) {
        recipePort.updateRecipesNicknames(nicknameDto);
    }


}

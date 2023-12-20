package com.recipia.recipe.hexagonal.application.service.feign;

import com.recipia.recipe.hexagonal.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.hexagonal.application.port.out.RecipePort;
import com.recipia.recipe.hexagonal.domain.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberChangedNicknameService {

    private final RecipePort recipePort;

    /**
     * FeignClient로부터 받아온 변경된 닉네임을 모든 레시피 엔티티에 적용시키는 로직
     */
    public void nicknameChange(NicknameDto nicknameDto, Long memberId) {

        // NicknameDto 처리 로직
        List<Recipe> recipeEntityList = recipePort.findRecipeByMemberIdAndDelYn(memberId, "N");
//
//        if (!recipeEntityList.isEmpty()) {
//            recipeEntityList.forEach(recipe -> recipe.changeNickname(nicknameDto.nickname()));
//        }
    }
}

package com.recipia.recipe.event.sqseventlistener;


import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.repository.RecipeRepository;
import com.recipia.recipe.event.springevent.NicknameChangeEvent;
import com.recipia.recipe.feign.MemberFeignClient;
import com.recipia.recipe.feign.dto.NicknameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RequestFeignListener {

    private final MemberFeignClient memberFeignClient;
    private final RecipeRepository recipeRepository;

    /**
     * Feign 클라이언트로 Member서버에 변경된 닉네임을 요청하는 리스너
     */
    @Transactional
    @EventListener
    public void requestMemberChangedNickname(NicknameChangeEvent event) {
        Long memberId = event.memberId();
        NicknameDto nicknameDto = memberFeignClient.getNickname(memberId);

        List<Recipe> recipeList = recipeRepository.findRecipeByMemberIdAndDelYn(memberId, "N");
        // 닉네임을 변경한 사용자가 작성한 레시피들의 nickname을 변경한다.
        if (!recipeList.isEmpty()) {
            recipeList.forEach(recipe -> {
                recipe.changeNickname(nicknameDto.nickname());
            });
        }
    }

}

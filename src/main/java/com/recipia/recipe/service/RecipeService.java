package com.recipia.recipe.service;

import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.repository.RecipeRepository;
import com.recipia.recipe.event.springevent.NicknameChangeEvent;
import com.recipia.recipe.exception.ApiErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void nicknameChange() {
        Recipe recipe = recipeRepository.findById(2L).orElseThrow(() -> new RuntimeException(ApiErrorCodeEnum.DB_ERROR.getMessage()));
        recipe.changeNickname("NEW-Recipe-NICKNAME-222");

        log.info("레시피 이름 변경 Service [레시피 pk : {}]", recipe.getId());
        eventPublisher.publishEvent(new NicknameChangeEvent(recipe.getId()));
    }

}



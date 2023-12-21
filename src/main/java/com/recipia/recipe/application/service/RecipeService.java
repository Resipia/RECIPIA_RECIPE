package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.out.persistence.RecipeEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.RecipeRepository;
import com.recipia.recipe.common.event.NicknameChangeEvent;
import com.recipia.recipe.common.exception.ApiErrorCodeEnum;
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
        RecipeEntity recipeEntity = recipeRepository.findById(2L).orElseThrow(() -> new RuntimeException(ApiErrorCodeEnum.DB_ERROR.getMessage()));
        recipeEntity.changeNickname("NEW-Recipe-NICKNAME-222");

        log.info("레시피 이름 변경 Service [레시피 pk : {}]", recipeEntity.getId());
        eventPublisher.publishEvent(new NicknameChangeEvent(recipeEntity.getId()));
    }

}



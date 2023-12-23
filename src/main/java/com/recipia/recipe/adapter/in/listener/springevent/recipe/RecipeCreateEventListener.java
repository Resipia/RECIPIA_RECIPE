package com.recipia.recipe.adapter.in.listener.springevent.recipe;

import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 레시피 저장을 할때 발행되는 스프링 이벤트를 구독하는 리스너 클래스
 * 헥사고날에서 controller 역할
 */
@Component
@RequiredArgsConstructor
public class RecipeCreateEventListener {

    private final CreateRecipeUseCase createRecipeUseCase;

    /**
     * MongoDB에 재료들을 저장한다.
     */
    @EventListener
    public void saveIngredientsIntoMongo(RecipeCreationEvent event) {

        createRecipeUseCase.saveIngredientsIntoMongo();
    }

    /**
     * MongoDB에 해시태그들을 저장한다.
     */
    @EventListener
    public void saveHashtagsIntoMongo(RecipeCreationEvent event) {


    }


}

package com.recipia.recipe.adapter.in.listener.springevent.recipe;

import com.recipia.recipe.common.event.RecipeCreationEvent;
import org.springframework.context.event.EventListener;

/**
 * 레시피 저장을 할때 발행되는 스프링 이벤트를 구독하는 리스너 클래스
 */
public class RecipeCreateEventListener {


    /**
     * MongoDB에 재료들을 저장한다.
     */
    @EventListener
    public void saveIngredientsIntoMongo(RecipeCreationEvent event) {


    }

    /**
     * MongoDB에 해시태그들을 저장한다.
     */
    @EventListener
    public void saveHashtagsIntoMongo(RecipeCreationEvent event) {


    }


}

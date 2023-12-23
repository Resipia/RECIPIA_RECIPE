package com.recipia.recipe.adapter.in.listener.springevent.recipe;

import com.recipia.recipe.common.event.RecipeCreationEvent;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;


@DisplayName("[통합] RecipeCreate 스프링 이벤트 구독자 테스트")
class RecipeCreateEventListenerTest extends TotalTestSupport {


    @Autowired
    private ApplicationContext applicationContext;

    @MockBean // 스프링 컨텍스트에서 가짜 객체를 관리합니다.
    private RecipeCreateEventListener recipeCreateEventListener;




    @DisplayName("레시피 생성 스프링 이벤트가 발행되면 이벤트 구독자가 동작한다.")
    @Test
    void whenRecipeCreationEventPublished_thenTriggerEventListenerMethods() {
        // given
        RecipeCreationEvent event = new RecipeCreationEvent("재료", "해시태그");

        // when
        applicationContext.publishEvent(event);

        // then
        Mockito.verify(recipeCreateEventListener).saveIngredientsIntoMongo(event);
    }


    @DisplayName("이벤트 구독자가 동작하면 재료를 저장한다.")
    @Test
    void whenTriggerEventListenerMethods_thenSaveIngredientsIntoMongo() {
        //given

        //when

        //then
    }

}
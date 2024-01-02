package com.recipia.recipe.adapter.in.listener.springevent.recipe;

import com.recipia.recipe.application.port.in.MongoUseCase;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@DisplayName("[통합] RecipeCreate 스프링 이벤트 리스너 테스트")
class SpringEventRecipeCreateListenerTest extends TotalTestSupport {

    @Autowired
    private ApplicationContext applicationContext;

    @SpyBean
    private SpringEventRecipeCreateListener springEventRecipeCreateListener;

    @MockBean
    private MongoUseCase mongoUseCase;

    @Test
    @DisplayName("레시피 생성 이벤트 발행시 리스너가 반응하여 동작하는지를 검증한다.")
    void whenEventPublished_thenEventListenerIsTriggered() {
        // given
        RecipeCreationEvent event = createEvent();

        // when
        applicationContext.publishEvent(event);

        // then
        Mockito.verify(springEventRecipeCreateListener).saveIngredientsIntoMongo(event);
    }

    @Test
    @DisplayName("레시피 생성 이벤트 발행시 리스너 메서드가 호출되고 그 내부의 재료를 저장하는 로직이 호출된다.")
    void whenRecipeCreationEventPublished_thenTriggerEventListenerMethods() {
        // given
        RecipeCreationEvent event = createEvent();

        // 실제 ArrayList<String> 인스턴스를 생성
        List<String> ingredients = new ArrayList<>();
        ingredients.add("김치");
        ingredients.add("감자");
        ingredients.add("고구마");

        // event.ingredients()가 호출될 때 실제 ingredients 리스트를 반환하도록 스텁 설정
        when(springEventRecipeCreateListener.splitIngredients(event.ingredients())).thenReturn(ingredients);

        // when
        applicationContext.publishEvent(event);

        // then
        Mockito.verify(mongoUseCase).saveIngredientsIntoMongo(ingredients);
    }

    private RecipeCreationEvent createEvent() {
        return new RecipeCreationEvent("김치, 감자, 고구마", "해시태그");
    }

}
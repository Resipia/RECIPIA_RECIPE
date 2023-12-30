package com.recipia.recipe.adapter.in.listener.springevent.recipe;

import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 레시피 저장을 할때 발행되는 스프링 이벤트를 구독하는 리스너 클래스
 * 헥사고날에서 controller 역할
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecipeCreateEventListener {

    private final CreateRecipeUseCase createRecipeUseCase;

    /**
     * MongoDB에 재료들을 저장한다.
     */
    @EventListener
    public void saveIngredientsIntoMongo(RecipeCreationEvent event) {

        // 1. 저장하기 전에 재료를 , 단위로 분리한다.
        List<String> ingredients = splitIngredients(event.ingredients());

        // 2.  저장을 시도한다.
        createRecipeUseCase.saveIngredientsIntoMongo(ingredients);

        log.info("데이터 저장 성공");
    }

    /**
     * MongoDB에 해시태그들을 저장한다.
     */
    @EventListener
    public void saveHashtagsIntoMongo(RecipeCreationEvent event) {
        // 여기서 저장하기 전에 해시태그를 , 단위로 분리하는 작업이 필요


    }


    // 테스트를 용이하게 하기 위해 method로 분리 (모든 예외상황은 왠만하면 dto에서 valid로 처리)
    public List<String> splitIngredients(String ingredients) {
        return Arrays.stream(ingredients.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }


}

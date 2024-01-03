package com.recipia.recipe.adapter.in.listener.springevent.recipe;

import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.application.port.in.MongoUseCase;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 레시피 저장을 할때 발행되는 스프링 이벤트를 구독하는 리스너 클래스
 * 헥사고날에서 controller 역할
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringEventRecipeCreateListener {

    private final MongoUseCase mongoUseCase;
    private final CreateRecipeUseCase createRecipeUseCase;

    /**
     * MongoDB에 재료 저장을 요청하는 스프링 이벤트 리스너
     * Optional.ofNullable을 사용하여 ingredients가 null이 아닌 경우에만 처리
     */
    @EventListener
    public void saveIngredientsIntoMongo(RecipeCreationEvent event) {
        Optional.ofNullable(event.ingredients())
                .filter(ingredients -> !ingredients.isBlank()) // 공백이 아닌 문자열에 대해서만 처리
                .ifPresent(this::processIngredients);
    }

    /**
     * MongoDB에 해시태그 저장을 요청하는 스프링 이벤트 리스너
     */
    @EventListener
    public void saveHashtagsIntoMongo(RecipeCreationEvent event) {
        // 여기서 저장하기 전에 해시태그를 , 단위로 분리하는 작업이 필요
        Optional.ofNullable(event.hashtags())
                .filter(hashtags -> !hashtags.isBlank()) // 공백이 아닌 문자열에 대해서만 처리
                .ifPresent(this::processHashtags);
    }

    /**
     * [extract] mongoDB에 재료 저장을 시도한다.
     */
    private void processIngredients(String ingredients) {
        // 1. 저장하기 전에 재료를 , 단위로 분리한다.
        List<String> splitIngredients = splitData(ingredients);

        // 2. 저장을 시도한다.
        mongoUseCase.saveIngredientsIntoMongo(splitIngredients);
    }

    /**
     * [extract] mongoDB에 해시태그 저장을 시도한다.
     */
    private void processHashtags(String hashtags) {
        // 1. 저장하기 전에 재료를 , 단위로 분리한다.
        List<String> splitHashtags = splitData(hashtags);

        // 2. 저장을 시도한다.
        mongoUseCase.saveHashtagsIntoMongo(splitHashtags);
    }

    // String 타입의 재료를 , 단위로 분리하여 리스트로 만들어준다.
    public List<String> splitData(String data) {
        return Arrays.stream(data.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }


}

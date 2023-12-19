package com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter;

import com.recipia.recipe.hexagonal.application.port.out.RecipePersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adapter 클래스는 port 인터페이스를 구현한다.
 * port에 요청이 들어가면 port의 메서드를 모두 구현한 이 adapter가 호출되어 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
public class RecipePersistenceAdapter implements RecipePersistencePort {

    // repository 주입
    private final RecipeRepository recipeRepository;



}
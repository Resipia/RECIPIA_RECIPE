package com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
import com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.hexagonal.application.port.out.RecipePort;
import com.recipia.recipe.hexagonal.domain.Recipe;
import com.recipia.recipe.hexagonal.domain.converter.RecipeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter 클래스는 port 인터페이스를 구현한다.
 * port에 요청이 들어가면 port의 메서드를 모두 구현한 이 adapter가 호출되어 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeAdapter implements RecipePort {

    // repository 주입
    private final RecipeRepository recipeRepository;
    private final RecipeQueryRepository recipeQueryRepository;

    @Override
    public List<Recipe> findRecipeByMemberIdAndDelYn(Long memberId, String delYn) {

        List<Recipe> data = recipeRepository.findRecipeByMemberIdAndDelYn(memberId, delYn)
                .stream().map(RecipeConverter::entityToDomain)
                .toList();

        RecipeEntity querydslData = recipeQueryRepository.findById(1L);


        return null;
    }

}
package com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
import com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.hexagonal.domain.Recipe;
import com.recipia.recipe.hexagonal.domain.converter.RecipeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles(value = "test")
@SpringBootTest
class RecipeAdapterTest {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeQueryRepository recipeQueryRepository;


    @Test
    public void test() {

        //given
        Long memberId = 1L;
        String delYn = "N";

        //when

        //then
        List<Recipe> data = recipeRepository.findRecipeByMemberIdAndDelYn(memberId, delYn)
                .stream().map(RecipeConverter::entityToDomain)
                .toList();

        RecipeEntity querydslData = recipeQueryRepository.findById(memberId);


        System.out.println();
    }


}
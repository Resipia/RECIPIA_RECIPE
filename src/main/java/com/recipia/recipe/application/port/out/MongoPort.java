package com.recipia.recipe.application.port.out;

import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;

import java.util.List;

public interface MongoPort {

    Long saveIngredientsIntoMongo(List<String> newIngredients);

    Long saveHashTagsIntoMongo(List<String> newHashtags);

    List<String> findIngredientsByPrefix(SearchRequestDto searchRequestDto);
}

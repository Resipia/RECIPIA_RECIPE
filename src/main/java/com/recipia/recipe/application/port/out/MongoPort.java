package com.recipia.recipe.application.port.out;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.adapter.in.search.dto.SearchResponseDto;

import java.util.List;

public interface MongoPort {

    Long saveIngredientsIntoMongo(List<String> newIngredients);

    Long saveHashTagsIntoMongo(List<String> newHashtags);

    SearchResponseDto searchData(SearchRequestDto searchRequestDto, SearchType searchType, String fieldName);

}

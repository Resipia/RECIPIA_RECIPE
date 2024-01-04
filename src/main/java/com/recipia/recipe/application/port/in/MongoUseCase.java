package com.recipia.recipe.application.port.in;

import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.adapter.in.search.dto.SearchResponseDto;

import java.util.List;

public interface MongoUseCase {

    // 스프링 이벤트 리스너가 사용: 몽고 db에 재료 저장
    void saveIngredientsIntoMongo(List<String> ingredients);

    void saveHashtagsIntoMongo(List<String> hashtags);

    List<SearchResponseDto> searchWordByPrefix(SearchRequestDto searchRequestDto);

}

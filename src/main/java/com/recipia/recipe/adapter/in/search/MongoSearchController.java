package com.recipia.recipe.adapter.in.search;

import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.application.port.in.MongoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/mongo")
@RequiredArgsConstructor
@RestController
public class MongoSearchController {

    private final MongoUseCase mongoUseCase;

    @GetMapping
    public String search(SearchRequestDto searchRequestDto) {
        mongoUseCase.findIngredientsByPrefix(searchRequestDto);
        return "";
    }

}

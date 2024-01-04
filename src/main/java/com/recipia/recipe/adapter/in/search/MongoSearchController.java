package com.recipia.recipe.adapter.in.search;

import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.application.port.in.MongoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 몽고DB 검색 컨트롤러
 */
@RequestMapping("/mongo")
@RequiredArgsConstructor
@RestController
public class MongoSearchController {

    private final MongoUseCase mongoUseCase;

    /**
     * 사용자가 입력한 단어의 접두사를 기준으로 몽고 db에서 연관된 단어를 검색한다.
     */
    @GetMapping
    public List<String> search(SearchRequestDto searchRequestDto) {
        return mongoUseCase.searchWordByPrefix(searchRequestDto);
    }

}

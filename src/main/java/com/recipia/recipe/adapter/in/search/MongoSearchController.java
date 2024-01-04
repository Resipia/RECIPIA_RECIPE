package com.recipia.recipe.adapter.in.search;

import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.adapter.in.search.dto.SearchResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.MongoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     * 기능: 사용자가 입력한 단어의 "접두사"를 기준으로 mongoDB에서 연관된 단어를 검색한다.
     * 설명:
     * 이 컨트롤러는 3가지 상황의 검색 페이지에서 사용되며 dto 내부의 조건(condition)에 따라 검색 결과가 달라진다.
     * 검색 조건은 3가지로 [전체, 재료, 해시태그] 이다.
     * 클라이언트는 이 컨트롤러를 호출할 때 검색 받고자 하는 결과의 개수(resultCount)를 담아서 보내줘야 한다. [5 or 10]
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseDto<List<SearchResponseDto>>> search(SearchRequestDto searchRequestDto) {
        List<SearchResponseDto> searchResponseDto = mongoUseCase.searchWordByPrefix(searchRequestDto);
        return ResponseEntity.ok(ResponseDto.success(searchResponseDto));
    }

}

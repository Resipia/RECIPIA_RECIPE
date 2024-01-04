package com.recipia.recipe.adapter.in.search.dto;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 검색 요청 dto
 */
@NoArgsConstructor
@Data
public class SearchRequestDto {

    private SearchType condition;  // 검색조건 (전체, 재료, 해시태그)
    private String searchWord; // 사용자가 입력한 검색어
    private int resultSize;    // 반환될 결과의 최대 개수 (5, 10)

    @Builder
    private SearchRequestDto(SearchType condition, String searchWord, int resultSize) {
        this.condition = condition;
        this.searchWord = searchWord;
        this.resultSize = resultSize;
    }

    public static SearchRequestDto of(SearchType condition, String searchWord, int resultSize) {
        return new SearchRequestDto(condition, searchWord, resultSize);
    }

    public static SearchRequestDto of(String searchWord, int resultSize) {
        return new SearchRequestDto(null, searchWord, resultSize);
    }

}

package com.recipia.recipe.adapter.in.search.dto;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponseDto {

    private SearchType type;  // 검색조건 (전체, 재료, 해시태그)
    private List<String> searchResultList;

    @Builder
    private SearchResponseDto(SearchType type, List<String> searchResultList) {
        this.type = type;
        this.searchResultList = searchResultList;
    }

    public static SearchResponseDto of(SearchType type, List<String> searchResultList) {
        return new SearchResponseDto(type, searchResultList);
    }

    public static SearchResponseDto of(List<String> searchResultList) {
        return new SearchResponseDto(null, searchResultList);
    }
}

package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.application.port.in.MongoUseCase;
import com.recipia.recipe.application.port.out.MongoPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class MongoService implements MongoUseCase {

    private final MongoPort mongoPort;

    /**
     * mongoDB에 데이터 저장을 담당하는 메서드
     * 주관심사: 레시피 생성 (mongoDB에 재료 데이터를 저장한다.)
     */
    @Override
    public void saveIngredientsIntoMongo(List<String> ingredients) {
        // List는 Optional보다는 if문으로 분기처리하는게 낫다. Optional은 단일 객체에 대한 null을 다룰때 주로 사용한다.
        if (ingredients == null || ingredients.isEmpty()) {
            throw new RecipeApplicationException(ErrorCode.INVALID_INGREDIENTS);
        }
        mongoPort.saveIngredientsIntoMongo(ingredients);
    }

    @Override
    public void saveHashtagsIntoMongo(List<String> hashtags) {
        // List는 Optional보다는 if문으로 분기처리하는게 낫다. Optional은 단일 객체에 대한 null을 다룰때 주로 사용한다.
        if (hashtags == null || hashtags.isEmpty()) {
            throw new RecipeApplicationException(ErrorCode.INVALID_HASHTAGS);
        }
        mongoPort.saveHashTagsIntoMongo(hashtags);
    }

    /**
     * mongoDB에서 재료 검색을 하는 기능
     */
    @Override
    public List<String> searchWordByPrefix(SearchRequestDto searchRequestDto) {
        // 1. 조건을 뽑아내고 null체크를 진행한다.
        SearchType condition = searchRequestDto.getCondition();
        validateSearchCondition(searchRequestDto);

        // 2. 조건에 따라 다른 검색을 실행한다.
        return switch (condition) {
            case ALL -> combineSearchResults(searchRequestDto, 5);
            case INGREDIENTS, HASHTAGS -> performSearch(searchRequestDto);
            default -> throw new RecipeApplicationException(ErrorCode.INVALID_SEARCH_CONDITION);
        };
    }

    // 검색 조건과 단어의 검증을 실시
    private void validateSearchCondition(SearchRequestDto searchRequestDto) {
        if (searchRequestDto.getSearchWord() == null || searchRequestDto.getSearchWord().isBlank()) {
            throw new RecipeApplicationException(ErrorCode.SEARCH_WORD_NECESSARY);
        }
        if (searchRequestDto.getCondition() == null) {
            throw new RecipeApplicationException(ErrorCode.CONDITION_NOT_FOUND);
        }
    }

    /**
     * 전체 검색일 경우에는 [재료 검색 결과 5개 + 해시태그 검색 결과 5개] 를 더한 10개의 결과 리스트를 만든다.
     */
    private List<String> combineSearchResults(SearchRequestDto searchRequestDto, int resultSizePerType) {
        SearchRequestDto ingredientsDto = createSearchDto(searchRequestDto, SearchType.INGREDIENTS, resultSizePerType);
        SearchRequestDto hashtagsDto = createSearchDto(searchRequestDto, SearchType.HASHTAGS, resultSizePerType);

        List<String> ingredientResults = mongoPort.searchData(ingredientsDto);
        List<String> hashtagResults = mongoPort.searchData(hashtagsDto);

        // 검색결과 합치기
        return Stream.concat(ingredientResults.stream(), hashtagResults.stream())
                .collect(Collectors.toList());
    }

    // dto를 새롭게 생성
    private SearchRequestDto createSearchDto(SearchRequestDto originalDto, SearchType type, int resultSize) {
        return SearchRequestDto.of(type, originalDto.getSearchWord(), resultSize);
    }

    // 전체가 아닐때 조건에 따른 검색 실시
    private List<String> performSearch(SearchRequestDto searchRequestDto) {
        return mongoPort.searchData(searchRequestDto);
    }




}

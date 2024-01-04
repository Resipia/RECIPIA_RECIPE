package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.adapter.in.search.dto.SearchResponseDto;
import com.recipia.recipe.application.port.in.MongoUseCase;
import com.recipia.recipe.application.port.out.MongoPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<SearchResponseDto> searchWordByPrefix(SearchRequestDto searchRequestDto) {
        // 1. dto의 데이터를 검증하고 검색 조건을 mongoDB의 필드명으로 변화시켜 준다.
        validateSearchCondition(searchRequestDto);
        SearchType condition = searchRequestDto.getCondition();
        String fieldName = mapToFieldName(condition);

        // 2. 검색 조건 [전체, 재료, 해시태그]에 따라 분기처리 실시
        return switch (condition) {
            // 전체 검색은 재료, 해시태그 검색결과 2개를 합친다.
            case ALL -> allSearch(searchRequestDto, 5);
            case INGREDIENT -> ingredientsOrHashtagSearch(searchRequestDto, SearchType.INGREDIENT, fieldName);
            case HASHTAG -> ingredientsOrHashtagSearch(searchRequestDto, SearchType.HASHTAG, fieldName);
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

    // ENUM 필드명을 실제 mongoDB에 저장할 필드명으로 바뀌준다.
    private String mapToFieldName(SearchType searchType) {
        return switch (searchType) {
            case INGREDIENT -> "ingredients";
            case HASHTAG -> "hashtags";
            default -> "ingredients";
        };
    }

    /**
     * 검색 조건이 [전체]일 경우
     * [재료 결과 5개 + 해시태그 결과 5개]를 합쳐서 총 10개의 결과값을 가진 리스트를 반환한다.
     */
    private List<SearchResponseDto> allSearch(SearchRequestDto searchRequestDto, Integer resultSize) {
        String searchWord = searchRequestDto.getSearchWord();

        // 재료와 해시태그 검색을 위한 DTO 생성
        SearchRequestDto ingredientsDto = createSearchDto(SearchType.INGREDIENT, searchWord, resultSize);
        SearchRequestDto hashtagsDto = createSearchDto(SearchType.HASHTAG, searchWord, resultSize);

        // 검색 결과를 가져온다.
        List<SearchResponseDto> searchIngredientResults = mongoPort.searchData(ingredientsDto, SearchType.INGREDIENT, "ingredients");
        List<SearchResponseDto> searchHashtagResults = mongoPort.searchData(hashtagsDto, SearchType.HASHTAG, "hashtags");

        // 두 결과 리스트를 합친다.
        List<SearchResponseDto> combinedResults = new ArrayList<>();
        combinedResults.addAll(searchIngredientResults);
        combinedResults.addAll(searchHashtagResults);
        return combinedResults;
    }


    /**
     * 검색 조건이 [재료, 해시태그]일 경우
     * 각 검색 조건에 따라 10개의 데이터를 리스트로 반환받는다.
     */
    private List<SearchResponseDto> ingredientsOrHashtagSearch(SearchRequestDto searchRequestDto, SearchType searchType, String fieldName) {
        return mongoPort.searchData(searchRequestDto, searchType, fieldName);
    }

    // dto를 새롭게 생성
    private SearchRequestDto createSearchDto(SearchType type, String searchWord, Integer resultSize) {
        return SearchRequestDto.of(type, searchWord, resultSize);
    }




}

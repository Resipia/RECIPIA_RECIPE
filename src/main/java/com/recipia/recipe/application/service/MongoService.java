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
        // 1. dto의 데이터를 검증하고 검색 조건을 mongoDB의 필드명으로 변화시켜 준다.
        validateSearchCondition(searchRequestDto);
        SearchType condition = searchRequestDto.getCondition();
        String fieldName = mapToFieldName(condition);

        // 2. 검색 조건 [전체, 재료, 해시태그]에 따라 분기처리 실시
        return switch (condition) {
            // 전체 검색은 재료, 해시태그 검색결과 2개를 합친다.
            case ALL -> allSearch(searchRequestDto, 5);
            case INGREDIENT, HASHTAG -> ingredientsOrHashtagSearch(searchRequestDto, fieldName);
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
    private List<String> allSearch(SearchRequestDto searchRequestDto, Integer resultSize) {

        String searchWord = searchRequestDto.getSearchWord();
        // [재료, 해시태그] 검색 요청에 사용할 dto 생성 (2개를 합칠 예정이라 resultSize는 상위 계층에서 5를 주입한다.)
        SearchRequestDto ingredientsDto = createSearchDto(SearchType.INGREDIENT, searchWord, resultSize);
        SearchRequestDto hashtagsDto = createSearchDto(SearchType.HASHTAG, searchWord, resultSize);

        // 검색 조건에 따른 [검색 결과]를 리스트로 반환한다.
        List<String> ingredientResults = mongoPort.searchData(ingredientsDto, "ingredients");
        List<String> hashtagResults = mongoPort.searchData(hashtagsDto, "hashtags");

        // 검색 결과를 합쳐서 하나의 리스트로 만들어 준다.
        return Stream.concat(ingredientResults.stream(), hashtagResults.stream())
                .collect(Collectors.toList());
    }

    /**
     * 검색 조건이 [재료, 해시태그]일 경우
     * 각 검색 조건에 따라 10개의 데이터를 리스트로 반환받는다.
     */
    private List<String> ingredientsOrHashtagSearch(SearchRequestDto searchRequestDto, String fieldName) {
        return mongoPort.searchData(searchRequestDto, fieldName);
    }

    // dto를 새롭게 생성
    private SearchRequestDto createSearchDto(SearchType type, String searchWord, Integer resultSize) {
        return SearchRequestDto.of(type, searchWord, resultSize);
    }




}

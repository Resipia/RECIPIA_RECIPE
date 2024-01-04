package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.search.constant.SearchType;
import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.adapter.in.search.dto.SearchResponseDto;
import com.recipia.recipe.application.port.out.MongoPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위] 몽고 서비스 테스트")
class MongoServiceTest {

    @InjectMocks
    MongoService sut;

    @Mock
    MongoPort mongoPort;

    @DisplayName("[happy] MongoDB에 재료 리스트 저장 시, MongoPort의 saveIngredientsIntoMongo를 호출한다.")
    @Test
    void saveIngredientsIntoMongo() {
        //given
        List<String> ingredients = Arrays.asList("재료1", "재료2");

        //when
        sut.saveIngredientsIntoMongo(ingredients);

        //then
        verify(mongoPort).saveIngredientsIntoMongo(ingredients);
    }

    @DisplayName("[bad] 비어있는 재료 리스트를 저장할 시 예외가 발생한다.")
    @Test
    void saveEmptyIngredientsIntoMongo() {
        //given
        List<String> emptyIngredients = Collections.emptyList();

        //when & then
        Assertions.assertThatThrownBy(() -> sut.saveIngredientsIntoMongo(emptyIngredients))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("재료가 유효하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INGREDIENTS);
    }

    @DisplayName("[bad] null인 재료 리스트를 저장할 시 예외가 발생한다.")
    @Test
    void saveNullIngredientsIntoMongo() {
        //given
        List<String> emptyIngredients = null;

        //when & then
        Assertions.assertThatThrownBy(() -> sut.saveIngredientsIntoMongo(emptyIngredients))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("재료가 유효하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INGREDIENTS);
    }

    @DisplayName("[bad] 비어있는 해시태그 리스트를 저장할 시 예외가 발생한다.")
    @Test
    void saveEmptyHashtagsIntoMongo() {
        //given
        List<String> emptyHashtags = Collections.emptyList();

        //when & then
        Assertions.assertThatThrownBy(() -> sut.saveHashtagsIntoMongo(emptyHashtags))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("해시태그가 유효하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_HASHTAGS);
    }

    @DisplayName("[bad] null인 해시태그 리스트를 저장할 시 예외가 발생한다.")
    @Test
    void saveNullHashtagsIntoMongo() {
        //given
        List<String> emptyHashtags = null;

        //when & then
        Assertions.assertThatThrownBy(() -> sut.saveHashtagsIntoMongo(emptyHashtags))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("해시태그가 유효하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_HASHTAGS);
    }

    @DisplayName("[bad] MongoPort 호출 중 DataAccessException 발생 시, 예외가 적절히 처리되는지 확인")
    @Test
    void testMongoPortExceptionHandling() {
        //given
        List<String> ingredients = Arrays.asList("고구마", "감자");
        doThrow(new DataAccessException("MongoDB 연결 오류") {})
                .when(mongoPort).saveIngredientsIntoMongo(ingredients);

        //when & then
        assertThrows(DataAccessException.class,
                () -> sut.saveIngredientsIntoMongo(ingredients));
    }

    @DisplayName("[happy] 접두사로 재료를 검색하면 올바른 결과를 반환한다.")
    @Test
    void findIngredientsByPrefixTest() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "김치", 10);
        SearchResponseDto mockResponse = SearchResponseDto.of(SearchType.INGREDIENT, Arrays.asList("김치", "김치가루"));
        String fieldName = "ingredients";
        when(mongoPort.searchData(dto, SearchType.INGREDIENT, fieldName)).thenReturn(mockResponse);

        //when
        List<SearchResponseDto> searchResponseDto = sut.searchWordByPrefix(dto);

        //then
        verify(mongoPort).searchData(dto, SearchType.INGREDIENT, fieldName);
        Assertions.assertThat(searchResponseDto).isEqualTo(List.of(mockResponse));
    }

    @DisplayName("[bad] 비어있는 접두사로 재료를 검색할 경우 예외가 발생한다.")
    @Test
    void findIngredientsByEmptyPrefixTest() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "", 10);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.searchWordByPrefix(dto))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("검색 단어 입력은 필수입니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEARCH_WORD_NECESSARY);
    }

    @DisplayName("[happy] 재료를 조건으로 검색하면 검색한 단어의 접두사로 시작되는 재료 리스트를 반환한다1.")
    @Test
    void searchWordByPrefix() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "김치", 10);
        SearchResponseDto searchResult = SearchResponseDto.of(SearchType.INGREDIENT, Arrays.asList("김치", "김밥", "김가루"));
        String fieldName = "ingredients";
        when(mongoPort.searchData(dto, SearchType.INGREDIENT, fieldName)).thenReturn(searchResult);

        //when
        List<SearchResponseDto> searchResponseDto = sut.searchWordByPrefix(dto);

        //then
        verify(mongoPort).searchData(dto, SearchType.INGREDIENT, fieldName);
        Assertions.assertThat(searchResponseDto.get(0).getSearchResultList().size()).isEqualTo(searchResult.getSearchResultList().size());
    }

    @DisplayName("[happy] 재료를 조건으로 검색하면 검색한 단어의 접두사로 시작되는 재료 리스트를 반환한다2.")
    @Test
    void searchWordByPrefix2() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "감자", 10);
        SearchResponseDto searchResult = SearchResponseDto.of(SearchType.INGREDIENT, Arrays.asList("감자", "감자채", "감자전분"));
        String fieldName = "ingredients";
        when(mongoPort.searchData(dto, SearchType.INGREDIENT, fieldName)).thenReturn(searchResult);

        //when
        List<SearchResponseDto> searchResponseDto = sut.searchWordByPrefix(dto);

        //then
        verify(mongoPort).searchData(dto, SearchType.INGREDIENT, fieldName);
        Assertions.assertThat(searchResponseDto.get(0).getSearchResultList().size()).isEqualTo(searchResult.getSearchResultList().size());
    }

    @DisplayName("[happy] 해시태그를 조건으로 검색하면 검색한 단어의 접두사로 시작되는 해시태그 리스트를 반환한다.")
    @Test
    void searchWordByPrefix_hashtag() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.HASHTAG, "감자", 10);
        SearchResponseDto searchResult = SearchResponseDto.of(SearchType.INGREDIENT, Arrays.asList("감자", "감자채", "감자전분"));
        String fieldName = "hashtags";
        when(mongoPort.searchData(dto, SearchType.HASHTAG, fieldName)).thenReturn(searchResult);

        //when
        List<SearchResponseDto> searchResponseDto = sut.searchWordByPrefix(dto);

        //then
        verify(mongoPort).searchData(dto, SearchType.HASHTAG, fieldName);
        Assertions.assertThat(searchResponseDto.get(0).getSearchResultList().size()).isEqualTo(searchResult.getSearchResultList().size());
    }

    @DisplayName("[happy] 전체 조건으로 검색하면 검색한 단어의 접두사로 시작되는 재료5개 해시태그5개의 결과를 가진 리스트를 반환한다.")
    @Test
    void searchWordByPrefix_all() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.ALL, "감자", 10);
        SearchRequestDto ingredientDto = SearchRequestDto.of(SearchType.INGREDIENT, "감자", 5);
        SearchRequestDto hashtagDto = SearchRequestDto.of(SearchType.HASHTAG, "감자", 5);

        SearchResponseDto searchResult = SearchResponseDto.of(SearchType.INGREDIENT, Arrays.asList("감자", "감자채", "감자전분"));

        when(mongoPort.searchData(ingredientDto, SearchType.INGREDIENT, "ingredients")).thenReturn(searchResult);
        when(mongoPort.searchData(hashtagDto, SearchType.HASHTAG, "hashtags")).thenReturn(searchResult);

        //when
        List<SearchResponseDto> searchResponseDto = sut.searchWordByPrefix(dto);

        //then
        verify(mongoPort, times(1)).searchData(ingredientDto, SearchType.INGREDIENT, "ingredients");
        verify(mongoPort, times(1)).searchData(hashtagDto, SearchType.HASHTAG, "hashtags");
//        Assertions.assertThat(searchResponseDto.get().getSearchResultList().size())
//                .isEqualTo(searchResult.get(0).getSearchResultList().size() + searchResult.get(1).getSearchResultList().size());
//        Assertions.assertThat(searchResponseDto.get(1).getSearchResultList()).containsAll(searchResult.get(1).getSearchResultList());
    }

    @DisplayName("[bad] 검색 조건 없이 검색 시 예외가 발생한다.")
    @Test
    void searchWordByPrefix_noCondition() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(null, "감자", 10);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.searchWordByPrefix(dto))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("검색 조건을 찾을 수 없습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONDITION_NOT_FOUND);
    }

    @DisplayName("[happy] 검색어가 빈 문자열인 경우 예외가 발생한다.")
    @Test
    void searchWordByPrefix_emptySearchWord() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, "", 10);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.searchWordByPrefix(dto))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("검색 단어 입력은 필수입니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEARCH_WORD_NECESSARY);
    }

    @DisplayName("[bad] 검색어가 null인 경우 예외가 발생한다.")
    @Test
    void searchWordByPrefix_nullSearchWord() {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENT, null, 10);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.searchWordByPrefix(dto))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("검색 단어 입력은 필수입니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEARCH_WORD_NECESSARY);
    }


}
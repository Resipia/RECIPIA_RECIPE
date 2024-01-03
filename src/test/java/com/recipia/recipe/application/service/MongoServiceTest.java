package com.recipia.recipe.application.service;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위] 몽고 서비스 테스트")
class MongoServiceTest {

    @InjectMocks
    MongoService sut;

    @Mock
    MongoPort mongoPort;

    @DisplayName("MongoDB에 재료 리스트 저장 시, MongoPort의 saveIngredientsIntoMongo를 호출한다.")
    @Test
    void saveIngredientsIntoMongo() {
        //given
        List<String> ingredients = Arrays.asList("재료1", "재료2");

        //when
        sut.saveIngredientsIntoMongo(ingredients);

        //then
        verify(mongoPort).saveIngredientsIntoMongo(ingredients);
    }

    @DisplayName("비어있는 재료 리스트를 저장할 시 예외가 발생한다.")
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

    @DisplayName("null인 재료 리스트를 저장할 시 예외가 발생한다.")
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

    @DisplayName("비어있는 해시태그 리스트를 저장할 시 예외가 발생한다.")
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

    @DisplayName("null인 해시태그 리스트를 저장할 시 예외가 발생한다.")
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

    @DisplayName("MongoPort 호출 중 DataAccessException 발생 시, 예외가 적절히 처리되는지 확인")
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
        String prefix = "김치";
        List<String> mockResponse = Arrays.asList("김치", "김치가루");
        when(mongoPort.findIngredientsByPrefix(prefix)).thenReturn(mockResponse);

        //when
        List<String> result = sut.findIngredientsByPrefix(prefix);

        //then
        verify(mongoPort).findIngredientsByPrefix(prefix);
        Assertions.assertThat(result).isEqualTo(mockResponse);
    }

    @DisplayName("[bad] 비어있는 접두사로 재료를 검색할 경우 빈 목록을 반환한다.")
    @Test
    void findIngredientsByEmptyPrefixTest() {
        //given
        String prefix = "";
        when(mongoPort.findIngredientsByPrefix(prefix)).thenReturn(Collections.emptyList());

        //when
        List<String> result = sut.findIngredientsByPrefix(prefix);

        //then
        verify(mongoPort).findIngredientsByPrefix(prefix);
        Assertions.assertThat(result).isEmpty();
    }


}
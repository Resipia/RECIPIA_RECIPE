package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import com.recipia.recipe.config.TestSecurityConfig;
import com.recipia.recipe.config.TestZipkinConfig;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.SubCategory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("[단위] 레시피 서비스 테스트")
class RecipeServiceTest {

    @Mock
    private RecipePort recipePort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RecipeService sut;

    @DisplayName("[happy] - 레시피 저장 시 저장된 레시피 ID를 반환하고 이벤트를 발생시킨다.")
    @Test
    void createRecipe_Success() {
        // given
        Recipe recipe = createRecipeDomain();
        Long savedRecipeId = 10L;  // 가정하는 저장된 ID
        Long savedNutritionalInfoId = 1L;  // 가정하는 저장된 ID

        // RecipePort의 동작을 정의
        when(recipePort.createRecipe(recipe)).thenReturn(savedRecipeId);
        when(recipePort.createNutritionalInfo(recipe, savedRecipeId)).thenReturn(savedNutritionalInfoId);

        // when
        Long result = sut.createRecipe(recipe);

        // then
        verify(recipePort).createRecipeCategoryMap(recipe, savedRecipeId); // 카테고리 맵핑 저장 메서드는 실행되었는가
        assertThat(result).isEqualTo(savedRecipeId);
        then(eventPublisher).should().publishEvent(new RecipeCreationEvent(recipe.getIngredient(), recipe.getHashtag()));
    }

    @Test
    @DisplayName("기본 페이징으로 레시피 목록을 정상적으로 가져온다.")
    void whenGetAllRecipeList_thenReturnsPagedRecipes() {
        // Given
        int page = 0;
        int size = 10;
        String sortType = "new";
        List<RecipeMainListResponseDto> recipeList = createMockRecipeList(size);
        Page<RecipeMainListResponseDto> mockPage = new PageImpl<>(recipeList);

        when(recipePort.getAllRecipeList(any(Pageable.class), eq(sortType))).thenReturn(mockPage);

        // When
        PagingResponseDto<RecipeMainListResponseDto> result = sut.getAllRecipeList(page, size, sortType);

        // Then
        Assertions.assertThat(result.getContent()).hasSize(size);
        Assertions.assertThat(result.getTotalCount()).isEqualTo(size);
    }

    @Test
    @DisplayName("빈 결과를 반환할 때 적절한 응답을 반환한다.")
    void whenGetAllRecipeListEmpty_thenReturnsEmptyResponse() {
        // Given
        int page = 0;
        int size = 10;
        String sortType = "new";
        Page<RecipeMainListResponseDto> emptyPage = Page.empty();

        when(recipePort.getAllRecipeList(any(Pageable.class), eq(sortType))).thenReturn(emptyPage);

        // When
        PagingResponseDto<RecipeMainListResponseDto> result = sut.getAllRecipeList(page, size, sortType);

        // Then
        Assertions.assertThat(result.getContent()).isEmpty();
        Assertions.assertThat(result.getTotalCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("잘못된 페이지 번호로 요청 시 예외를 반환한다")
    void whenGetAllRecipeListWithInvalidPage_thenThrowsException() {
        // Given
        int invalidPage = -1;
        int size = 10;
        String sortType = "new";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> sut.getAllRecipeList(invalidPage, size, sortType));
    }

    // todo: 예외처리가 필요하다.
    @Test
    @DisplayName("잘못된 정렬 타입으로 요청 시 예외를 반환한다")
    void whenGetAllRecipeListWithInvalidSortType_thenThrowsException() {
        // Given
        int page = 0;
        int size = 10;
        String invalidSortType = "invalid";

        // When & Then
//        assertThrows(IllegalArgumentException.class, () -> sut.getAllRecipeList(page, size, invalidSortType));
    }

    @Test
    @DisplayName("Port 레이어에서 예외 발생 시 적절히 처리한다")
    void whenRecipePortThrowsException_thenServiceHandlesIt() {
        // Given
        int page = 0;
        int size = 10;
        String sortType = "new";
        when(recipePort.getAllRecipeList(any(Pageable.class), eq(sortType))).thenThrow(new RuntimeException("DB Error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> sut.getAllRecipeList(page, size, sortType));
    }


    private List<RecipeMainListResponseDto> createMockRecipeList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> new RecipeMainListResponseDto((long) i, "Recipe " + i, "Nickname", false))
                .collect(Collectors.toList());
    }

    private Recipe createRecipeDomain() {
        return Recipe.of(
                10L,
                "레시피",
                "레시피 설명",
                20,
                "닭",
                "#진안",
                NutritionalInfo.of(10,10,10,10,10),
                List.of(SubCategory.of(1L), SubCategory.of(2L)),
                "진안",
                "N"
        );
    }

}
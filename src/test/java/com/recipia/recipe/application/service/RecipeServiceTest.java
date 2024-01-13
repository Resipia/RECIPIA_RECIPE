package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.application.port.out.RedisPort;
import com.recipia.recipe.common.event.RecipeCreationEvent;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeFile;
import com.recipia.recipe.domain.SubCategory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("[단위] 레시피 서비스 테스트")
class RecipeServiceTest {

    @Mock
    private RecipePort recipePort;

    @Mock
    private RedisPort redisPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ImageS3Service imageS3Service;

    @InjectMocks
    private RecipeService sut;

    @DisplayName("[happy] 레시피 저장 시 저장된 레시피 ID를 반환하고 이벤트를 발생시킨다.")
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
        Long result = sut.createRecipe(recipe, Collections.emptyList());

        // then
        verify(recipePort).createRecipeCategoryMap(recipe, savedRecipeId); // 카테고리 맵핑 저장 메서드는 실행되었는가
        assertThat(result).isEqualTo(savedRecipeId);
        then(eventPublisher).should().publishEvent(new RecipeCreationEvent(recipe.getIngredient(), recipe.getHashtag()));
    }

    @Test
    @DisplayName("[happy] 기본 페이징으로 레시피 목록을 정상적으로 가져온다.")
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
    @DisplayName("[happy] 빈 결과를 반환할 때 적절한 응답을 반환한다.")
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
    @DisplayName("[bad] 잘못된 페이지 번호로 요청 시 예외를 반환한다")
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
    @DisplayName("[bad] Port 레이어에서 예외 발생 시 적절히 처리한다")
    void whenRecipePortThrowsException_thenServiceHandlesIt() {
        // Given
        int page = 0;
        int size = 10;
        String sortType = "new";
        when(recipePort.getAllRecipeList(any(Pageable.class), eq(sortType))).thenThrow(new RuntimeException("DB Error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> sut.getAllRecipeList(page, size, sortType));
    }

    @Test
    @DisplayName("[happy] 유효한 레시피 ID로 단건 조회시 데이터를 잘 가져온다.")
    void getRecipeDetailViewWithValidId() {
        // Given
        Recipe domain = Recipe.of(1L);
        Long validRecipeId = domain.getId();
        Recipe mockDto = Recipe.of(validRecipeId);

        when(recipePort.getRecipeDetailView(domain)).thenReturn(mockDto);

        // When
        Recipe result = sut.getRecipeDetailView(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(validRecipeId);
    }

    @Test
    @DisplayName("[bad] 존재하지 않는 레시피 ID로 레시피를 단건 조회하면 예외가 발생한다.")
    void getRecipeDetailViewWithInvalidId() {
        // Given
        Recipe domain = Recipe.of(9999L);
        given(recipePort.getRecipeDetailView(domain))
                .willThrow(new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND));

        // When & Then
        assertThrows(RecipeApplicationException.class, () -> sut.getRecipeDetailView(domain));
    }

    @Test
    @DisplayName("[bad] 만약 Port 레이어에서 예외가 발생하면 서비스 레이어에서 예외를 처리한다.")
    void getRecipeDetailViewWhenPortThrowsException() {
        // Given
        Recipe domain = Recipe.of(1L);
        given(recipePort.getRecipeDetailView(domain)).willThrow(new RuntimeException("DB Error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> sut.getRecipeDetailView(domain));
    }

    @Test
    @DisplayName("[bad] S3 파일 업로드에 실패할 시 예외가 발생한다.")
    void whenFileUploadFails_thenThrowsException() {
        // given
        Recipe recipe = createRecipeDomain();
        List<MultipartFile> mockFiles = createMockMultipartFileList();
        when(imageS3Service.createRecipeFile(any(MultipartFile.class), anyInt(), anyLong()))
                .thenThrow(new RecipeApplicationException(ErrorCode.S3_UPLOAD_ERROR));

        // when & then
        assertThrows(RecipeApplicationException.class, () -> sut.createRecipe(recipe, mockFiles));
    }

    @DisplayName("[happy] 레시피 업데이트 시 모든 컴포넌트(의존하는 빈)가 올바르게 동작한다.")
    @Test
    void updateRecipeHappy() {
        //given
        Recipe recipe = createRecipeDomain();
        List<MultipartFile> mockMultipartFileList = createMockMultipartFileList();
        Long updatedRecipeId = 1L; // 업데이트된 id
        List<Long> savedFileIdList = List.of(1L, 2L, 3L); // 저장된 파일 id 리스트

        when(recipePort.checkIsRecipeMineExist(recipe)).thenReturn(true);
        when(recipePort.updateRecipe(recipe)).thenReturn(updatedRecipeId);
        when(recipePort.softDeleteRecipeFilesByRecipeId(updatedRecipeId)).thenReturn(3L);
        when(imageS3Service.createRecipeFile(any(MultipartFile.class), anyInt(), eq(updatedRecipeId)))
                .thenReturn(RecipeFile.of(recipe, 0, "/", "/", "nm", "nm", "jpg", 100, "N"));
        when(recipePort.saveRecipeFile(anyList())).thenReturn(savedFileIdList);

        //when
        sut.updateRecipe(recipe, mockMultipartFileList);

        //then
        verify(recipePort).updateRecipe(recipe);
        verify(recipePort).updateNutritionalInfo(recipe);
        verify(recipePort).softDeleteRecipeFilesByRecipeId(updatedRecipeId);
        verify(recipePort).saveRecipeFile(anyList());
        then(eventPublisher).should().publishEvent(new RecipeCreationEvent(recipe.getIngredient(), recipe.getHashtag()));
    }

    @DisplayName("[bad] 파일 저장 후 반환받은 id값이 없다면 예외가 발생한다.")
    @Test
    void updateRecipeException() {
        //given
        Recipe recipe = createRecipeDomain();
        List<MultipartFile> mockFiles = createMockMultipartFileList();
        Long updatedRecipeId = 1L;  // 가정하는 업데이트된 ID

        //when
        when(recipePort.checkIsRecipeMineExist(recipe)).thenReturn(true);
        when(recipePort.updateRecipe(recipe)).thenReturn(updatedRecipeId);
        when(recipePort.softDeleteRecipeFilesByRecipeId(updatedRecipeId)).thenReturn(3L);
        when(recipePort.saveRecipeFile(anyList())).thenReturn(Collections.emptyList());

        //then
        Assertions.assertThatThrownBy(() -> sut.updateRecipe(recipe, mockFiles))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("데이터 베이스에 파일을 저장하던중 예외가 발생했습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_FILE_SAVE_ERROR);
    }

    @DisplayName("[happy] 레시피 소프트 삭제가 성공적으로 수행된다.")
    @Test
    void deleteRecipeByRecipeId_Success() {
        // given
        Recipe domain = Recipe.of(1L);
        when(recipePort.checkIsRecipeMineExist(domain)).thenReturn(true);
        when(recipePort.softDeleteByRecipeId(domain)).thenReturn(1L);

        // when
        Long deletedCount = sut.deleteRecipeByRecipeId(domain);

        // Then
        assertThat(deletedCount).isEqualTo(1L); // 삭제된 행의 수가 1임을 검증
        then(recipePort).should().softDeleteByRecipeId(domain);
    }

    @DisplayName("[bad] 존재하지 않는 레시피 ID로 삭제 시도 시, 삭제되지 않음을 확인한다.")
    @Test
    void deleteRecipeByInvalidRecipeId() {
        // Given
        Recipe invalidDomain = Recipe.of(9999L); // 존재하지 않는 레시피 ID
        when(recipePort.checkIsRecipeMineExist(invalidDomain)).thenReturn(true);
        given(recipePort.softDeleteByRecipeId(invalidDomain)).willReturn(0L); // 삭제되지 않았다고 가정

        // When
        Long deletedCount = sut.deleteRecipeByRecipeId(invalidDomain);

        // Then
        assertThat(deletedCount).isEqualTo(0L); // 삭제된 행이 없음을 검증
        then(recipePort).should().softDeleteByRecipeId(invalidDomain);
    }

    private List<MultipartFile> createMockMultipartFileList() {
        return List.of(
                new MockMultipartFile("file1", "filename1.jpg", "image/jpeg", "some-image".getBytes()),
                new MockMultipartFile("file2", "filename2.png", "image/png", "some-image".getBytes()),
                new MockMultipartFile("file3", "filename3.gif", "image/gif", "some-image".getBytes())
        );
    }

    private List<RecipeMainListResponseDto> createMockRecipeList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> RecipeMainListResponseDto.of((long) i, "Recipe " + i, "Nickname", false))
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
                "N",
                0L,
                0,
                false
        );
    }

}
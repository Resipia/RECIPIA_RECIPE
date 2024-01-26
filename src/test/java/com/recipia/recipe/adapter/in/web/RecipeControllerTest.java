package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateUpdateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.application.port.in.DeleteRecipeUseCase;
import com.recipia.recipe.application.port.in.ReadRecipeUseCase;
import com.recipia.recipe.application.port.in.UpdateRecipeUseCase;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.SubCategory;
import com.recipia.recipe.domain.converter.RecipeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[통합] 레시피 컨트롤러")
@AutoConfigureMockMvc // @AutoConfigureMockMvc를 사용하여 MockMvc를 이용해 HTTP 요청을 모의로 보낸다.
class RecipeControllerTest extends TotalTestSupport {

    @Autowired private MockMvc mockMvc;
    @MockBean RecipeConverter converter;
    @MockBean private ReadRecipeUseCase readRecipeUseCase;
    @MockBean CreateRecipeUseCase createRecipeUseCase;
    @MockBean UpdateRecipeUseCase updateRecipeUseCase;
    @MockBean DeleteRecipeUseCase deleteRecipeUseCase;
    private ObjectMapper objectMapper = new ObjectMapper();


    @DisplayName("[happy] 유저가 레시피 생성 요청 시 정상적으로 저장되고 성공 응답을 반환한다")
    @Test
    void ifUserCreateRecipeShouldComplete() throws Exception {
        //given
        RecipeCreateUpdateRequestDto recipeCreateUpdateRequestDto = RecipeCreateUpdateRequestDto.of("고구마찜", "고구마찜이다");
        Recipe domain = createRecipeDomain();

        when(converter.dtoToDomainCreate(recipeCreateUpdateRequestDto)).thenReturn(domain);
        when(createRecipeUseCase.createRecipe(domain, Collections.emptyList())).thenReturn(2L);

        //when & then
        mockMvc.perform(post("/recipe/createRecipe")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("recipeCreateUpdateRequestDto", recipeCreateUpdateRequestDto))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[happy] 전체 레시피 목록 조회 요청 시 정상적으로 페이징된 데이터와 성공 응답을 반환한다")
    @Test
    void test() throws Exception {
        //given
        RecipeListResponseDto dto = RecipeListResponseDto.of("레시피명", "닉네임", null, null, null, "2020-12-12");
        PagingResponseDto<RecipeListResponseDto> pagingResponseDto = PagingResponseDto.of(List.of(dto), 100L);

        when(readRecipeUseCase.getAllRecipeList(0, 10, "new", null)).thenReturn(pagingResponseDto);

        //when & then
        mockMvc.perform(get("/recipe/getAllRecipeList")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "new")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalCount").value(pagingResponseDto.getTotalCount()));
    }

//    @Test
//    @DisplayName("유효한 레시피 ID로 단건 조회 시, 성공적으로 데이터와 성공 응답을 반환한다.")
//    void getRecipeDetailViewWithValidId() throws Exception {
//        //given
//        Long validRecipeId = 1L;
//        RecipeDetailViewDto mockDetail = new RecipeDetailViewDto(validRecipeId, "Test Recipe", "Test Nickname", "Test Description", false);
//        when(readRecipeUseCase.getRecipeDetailView(eq(validRecipeId))).thenReturn(mockDetail);
//
//        //when & then
//        mockMvc.perform(get("/recipe/getRecipeDetail")
//                        .param("recipeId", String.valueOf(validRecipeId))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result.id").value(validRecipeId))
//                .andExpect(jsonPath("$.result.recipeName").value(mockDetail.getRecipeName()))
//                .andExpect(jsonPath("$.result.nickname").value(mockDetail.getNickname()))
//                .andExpect(jsonPath("$.result.recipeDesc").value(mockDetail.getRecipeDesc()))
//                .andExpect(jsonPath("$.result.bookmarked").value(mockDetail.isBookmarked()));
//    }

    @Test
    @DisplayName("존재하지 않는 레시피 ID로 조회 시, 적절한 예외 응답을 반환한다.")
    void getRecipeDetailViewWithInvalidId() throws Exception {
        //given
        Recipe domain = Recipe.of(9999L);
        when(readRecipeUseCase.getRecipeDetailView(eq(domain))).thenThrow(new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND));

        //when & then
        mockMvc.perform(get("/recipe/getRecipeDetail")
                        .param("recipeId", String.valueOf(domain.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("[happy] 레시피 업데이트 요청 시 정상적으로 처리되고 성공 응답을 반환한다.")
    void updateRecipeSuccess() throws Exception {
        //given
        RecipeCreateUpdateRequestDto recipeCreateUpdateRequestDto = RecipeCreateUpdateRequestDto.of("고구마찜", "고구마찜이다");
        Recipe domain = createRecipeDomain();

        //when
        when(converter.dtoToDomainUpdate(recipeCreateUpdateRequestDto)).thenReturn(domain);

        //then
        mockMvc.perform(put("/recipe/updateRecipe")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("recipeCreateUpdateRequestDto", recipeCreateUpdateRequestDto))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[bad] 잘못된 레시피 업데이트 요청 시 적절한 예외 응답을 반환한다.")
    void updateRecipeFailure() throws Exception {
        // given
        RecipeCreateUpdateRequestDto recipeCreateUpdateRequestDto = RecipeCreateUpdateRequestDto.of("", "고구마찜이다");
        Recipe domain = createRecipeDomain();

        // when & then
        mockMvc.perform(put("/recipe/updateRecipe")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("recipeCreateUpdateRequestDto", recipeCreateUpdateRequestDto))
                .andExpect(status().is4xxClientError());
    }


    private Recipe createRecipeDomain() {
        return Recipe.of(
                1L,
                "레시피",
                "레시피 설명",
                20,
                "닭",
                "#진안",
                NutritionalInfo.of(10, 10, 10, 10, 10),
                List.of(SubCategory.of(1L), SubCategory.of(2L)),
                "진안",
                "N",
                0L,
                0,
                null,
                Collections.emptyList(),
                LocalDateTime.now()
        );
    }


}
package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.application.port.in.ReadRecipeUseCase;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[통합] 레시피 컨트롤러")
@AutoConfigureMockMvc // @AutoConfigureMockMvc를 사용하여 MockMvc를 이용해 HTTP 요청을 모의로 보낸다.
class RecipeControllerTest extends TotalTestSupport {

    @Autowired private MockMvc mockMvc;
    @MockBean RecipeConverter converter;
    @MockBean private ReadRecipeUseCase readRecipeUseCase;
    @MockBean CreateRecipeUseCase createRecipeUseCase;
    private ObjectMapper objectMapper = new ObjectMapper();


    @DisplayName("[happy] 유저가 레시피 생성 요청 시 정상적으로 저장되고 성공 응답을 반환한다")
    @Test
    void ifUserCreateRecipeShouldComplete() throws Exception {
        //given
        RecipeCreateRequestDto recipeCreateRequestDto = RecipeCreateRequestDto.of("고구마찜", "고구마찜이다");
        Recipe domain = createRecipeDomain();

        when(converter.requestDtoToDomain(recipeCreateRequestDto)).thenReturn(domain);
        when(createRecipeUseCase.createRecipe(domain)).thenReturn(2L);

        // MockMvc 테스트
        mockMvc.perform(post("/recipe/createRecipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recipeCreateRequestDto)))// 실제 DTO 객체를 JSON으로 변환
                .andExpect(status().isOk());
    }

    @DisplayName("[happy] 전체 레시피 목록 조회 요청 시 정상적으로 페이징된 데이터와 성공 응답을 반환한다")
    @Test
    void test() throws Exception {
        //given
        RecipeMainListResponseDto dto = RecipeMainListResponseDto.of("레시피명", "닉네임", false);
        PagingResponseDto<RecipeMainListResponseDto> pagingResponseDto = PagingResponseDto.of(List.of(dto), 100L);

        when(readRecipeUseCase.getAllRecipeList(anyInt(), anyInt(), eq("new"))).thenReturn(pagingResponseDto);

        //when & then
        mockMvc.perform(get("/recipe/getAllRecipeList")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "new")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalCount").value(pagingResponseDto.getTotalCount()));
    }

    private Recipe createRecipeDomain() {
        return Recipe.of(
                "레시피",
                "레시피 설명",
                20,
                "닭",
                "#진안",
                NutritionalInfo.of(10,10,10,10,10),
                List.of(SubCategory.of(1L), SubCategory.of(2L)),
                "N"
        );
    }
    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeRequestDto;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.converter.RecipeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("레시피 컨트롤러")
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc // @AutoConfigureMockMvc를 사용하여 MockMvc를 이용해 HTTP 요청을 모의로 보낸다.
class RecipeControllerTest extends TotalTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateRecipeUseCase createRecipeUseCase;


    @DisplayName("유저가 레시피 생성 요청 시 정상적으로 저장되고 성공 응답을 반환한다")
    @Test
    void ifUserCreateRecipeShouldComplete() throws Exception {
        // Given
        RecipeRequestDto requestDto = createRecipeRequestDto();
        Recipe recipe = RecipeConverter.dtoToDomain(requestDto);

        // fixme: 유저정보는 securityContextHolder에서 꺼내니까 이걸 잘 구분해라
        recipe.change(1L, "진안");

        // When
        given(createRecipeUseCase.createRecipe(recipe)).willReturn(1L);

        // Then
        mockMvc.perform(post("/recipe/createRecipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk()) // 응답 상태 코드가 200 OK인지 확인한다.
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"));
    }

    private RecipeRequestDto createRecipeRequestDto() {
        return RecipeRequestDto.of("레시피", "레시피 설명", 20, "감자", "고구마", "{당: 많음}", "N");
    }


}
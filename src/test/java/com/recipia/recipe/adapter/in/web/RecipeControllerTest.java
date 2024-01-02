package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.NutritionalInfoDto;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeCreateRequestDto;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.config.TestJwtConfig;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.NutritionalInfo;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.converter.RecipeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[통합] 레시피 컨트롤러")
@AutoConfigureMockMvc // @AutoConfigureMockMvc를 사용하여 MockMvc를 이용해 HTTP 요청을 모의로 보낸다.
class RecipeControllerTest extends TotalTestSupport {

    @Autowired private MockMvc mockMvc;
    @MockBean RecipeConverter converter;
    @MockBean CreateRecipeUseCase createRecipeUseCase;

    private ObjectMapper objectMapper = new ObjectMapper();


    @DisplayName("유저가 레시피 생성 요청 시 정상적으로 저장되고 성공 응답을 반환한다")
    @Test
    void ifUserCreateRecipeShouldComplete() throws Exception {
        //given
        RecipeCreateRequestDto recipeCreateRequestDto = RecipeCreateRequestDto.of("고구마찜", "고구마찜이다");
        Recipe domain = createRecipeDomain();
        when(converter.requestDtoToDomain(recipeCreateRequestDto)).thenReturn(domain);
        when(createRecipeUseCase.createRecipe(domain)).thenReturn(1L);

        // MockMvc 테스트
        mockMvc.perform(post("/recipe/createRecipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recipeCreateRequestDto)))// 실제 DTO 객체를 JSON으로 변환
                .andExpect(status().isOk());
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
                "진안",
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
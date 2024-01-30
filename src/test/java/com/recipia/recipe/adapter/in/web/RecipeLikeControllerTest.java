package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.RecipeLikeRequestDto;
import com.recipia.recipe.application.port.in.RecipeLikeUseCase;
import com.recipia.recipe.config.TestSecurityConfig;
import com.recipia.recipe.config.TestZipkinConfig;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.RecipeLike;
import com.recipia.recipe.domain.converter.RecipeLikeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[통합] 좋아요 컨트롤러 테스트")
@AutoConfigureMockMvc // @AutoConfigureMockMvc를 사용하여 MockMvc를 이용해 HTTP 요청을 모의로 보낸다.
class RecipeLikeControllerTest extends TotalTestSupport {

    @MockBean
    private RecipeLikeConverter recipeLikeConverter;

    @MockBean
    private RecipeLikeUseCase recipeLikeUseCase;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("[통합] 유저가 좋아요 요청을 시도하면 정상적으로 처리된다.")
    void testRecipeLikeRequest() throws Exception {
        // given
        RecipeLikeRequestDto requestDto = RecipeLikeRequestDto.of(null, 1L);
        RecipeLike domain = RecipeLike.of(Recipe.of(null), 1L);
        when(recipeLikeConverter.dtoToDomain(any(RecipeLikeRequestDto.class))).thenReturn(domain);
        when(recipeLikeUseCase.recipeLikeProcess(domain)).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/recipe/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
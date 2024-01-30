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
@Import({TestSecurityConfig.class, TestZipkinConfig.class})   // 테스트 설정 클래스 적용
@WebMvcTest(RecipeLikeController.class) // 특정 컨트롤러에 대한 웹 레이어만 로드
class RecipeLikeControllerTest {

    @MockBean
    private RecipeLikeConverter recipeLikeConverter;

    @MockBean
    private RecipeLikeUseCase recipeLikeUseCase;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("[통합] 유저가 유효하지 않은 데이터로 좋아요 요청을 시도하면 검증 실패 응답이 반환된다.")
    void testInvalidRecipeLikeRequest() throws Exception {
        // given
        // 잘못된 데이터를 포함한 요청 객체 생성 (예: recipeId 또는 memberId가 null)
        RecipeLikeRequestDto requestDto = RecipeLikeRequestDto.of(null, null, 1L);

        // when & then
        mockMvc.perform(post("/recipe/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest()); // 잘못된 요청에 대해 400 Bad Request 응답을 기대함
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.MyPageRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.application.port.in.MyPageUseCase;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.MyPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[통합] 마이페이지 컨트롤러 테스트")
@AutoConfigureMockMvc
class MyPageControllerTest extends TotalTestSupport {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MyPageUseCase myPageUseCase;
    @MockBean
    private SecurityUtil securityUtil;
    private ObjectMapper objectMapper = new ObjectMapper();


    @DisplayName("[happy] targetMemberId가 작성한 레시피 갯수를 요청하면 성공 응답을 반환한다.")
    @Test
    void getRecipeCount() throws Exception {
        // given
        MyPageRequestDto dto = MyPageRequestDto.of(1L);
        MyPage domain = MyPage.of(1L);
        when(myPageUseCase.getRecipeCount(dto.getTargetMemberId())).thenReturn(domain);

        //when & then
        mockMvc.perform(post("/recipe/mypage/recipeCnt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("[happy] 마이페이지 주인인 targetMemberId가 작성한 레시피중 조회수가 가장 높은 top5 레시피 목록을 가져온다.")
    @Test
    void getHighRecipe() throws Exception {
        // given
        MyPageRequestDto dto = MyPageRequestDto.of(1L);
        RecipeListResponseDto listDto = RecipeListResponseDto.of("레시피명", "닉네임", null, null, null, LocalDateTime.now());
        when(myPageUseCase.getTargetMemberRecipeHigh(dto.getTargetMemberId())).thenReturn(List.of(listDto));

        //when & then
        mockMvc.perform(post("/recipe/mypage/highRecipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("[happy] targetMember가 작성한 레시피 목록 조회 요청 시 정상적으로 페이징된 데이터와 성공 응답을 반환한다")
    @Test
    void getAllTargetMemberRecipeList() throws Exception {
        //given
        RecipeListResponseDto dto = RecipeListResponseDto.of("레시피명", "닉네임", null, null, null, LocalDateTime.now());
        PagingResponseDto<RecipeListResponseDto> pagingResponseDto = PagingResponseDto.of(List.of(dto), 100L);

        when(myPageUseCase.getTargetMemberRecipeList(0, 10, "new", 1L)).thenReturn(pagingResponseDto);

        //when & then
        mockMvc.perform(get("/recipe/mypage/targetMemberRecipeList")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "new")
                        .param("targetMemberId", "1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalCount").value(pagingResponseDto.getTotalCount()));
    }


    @DisplayName("[happy] 내가 북마크한한 레시피 목록 조회 요청 시 정상적으로 페이징된 데이터와 성공 응답을 반환한다")
    @Test
    void getAllMyBookmarkRecipeList() throws Exception {
        //given
        RecipeListResponseDto dto = RecipeListResponseDto.of("레시피명", "닉네임", null, null, null, LocalDateTime.now());
        PagingResponseDto<RecipeListResponseDto> pagingResponseDto = PagingResponseDto.of(List.of(dto), 100L);

        when(myPageUseCase.getAllMyBookmarkList(0, 10)).thenReturn(pagingResponseDto);

        //when & then
        mockMvc.perform(get("/recipe/mypage/myBookmarkRecipeList")
                        .param("page", "0")
                        .param("size", "10")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalCount").value(pagingResponseDto.getTotalCount()));
    }

    // JSON 문자열 변환을 위한 유틸리티 메서드
    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
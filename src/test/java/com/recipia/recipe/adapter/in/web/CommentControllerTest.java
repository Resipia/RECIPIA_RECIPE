package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.CommentDeleteRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.CommentRegistRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.CommentUpdateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.SubCommentRegistRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.application.port.in.CommentUseCase;
import com.recipia.recipe.application.port.in.SubCommentUseCase;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.SubComment;
import com.recipia.recipe.domain.converter.CommentConverter;
import com.recipia.recipe.domain.converter.SubCommentConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[통합] 댓글 컨트롤러 테스트")
@AutoConfigureMockMvc
class CommentControllerTest extends TotalTestSupport {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentConverter commentConverter;
    @MockBean
    private CommentUseCase commentUseCase;
    @MockBean
    private SubCommentConverter subCommentConverter;
    @MockBean
    private SubCommentUseCase subCommentUseCase;
    private ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("[happy] 유저가 댓글 등록 요청 시 정상적으로 저장되고 성공 응답을 반환한다.")
    @Test
    void ifUserCreateCommentShouldComplete() throws Exception {
        // given
        CommentRegistRequestDto dto = CommentRegistRequestDto.of(1L, "commentvalue");
        Comment domain = Comment.of(null, 1L, 1L, "commentvalue", "N");

        when(commentConverter.registRequestDtoToDomain(dto)).thenReturn(domain);
        when(commentUseCase.createComment(domain)).thenReturn(1L);

        //when & then
        mockMvc.perform(post("/recipe/regist/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @DisplayName("[happy] 유저가 댓글 수정 요청 시 정상적으로 저장되고 성공 응답을 반환한다.")
    @Test
    void ifUserUpdateCommentShouldComplete() throws Exception {
        // given
        CommentUpdateRequestDto dto = CommentUpdateRequestDto.of(1L, "update-value");
        Comment domain = Comment.of(dto.getId(), null, 1L, dto.getCommentText(), "N");

        when(commentConverter.updateRequestDtoToDomain(dto)).thenReturn(domain);
        when(commentUseCase.updateComment(domain)).thenReturn(1L);

        //when & then
        mockMvc.perform(post("/recipe/update/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @DisplayName("[happy] 유저가 댓글 삭제 요청 시 정상적으로 삭제하고 성공 응답을 반환한다.")
    @Test
    void ifUserDeleteCommentShouldComplete() throws Exception {
        // given
        CommentDeleteRequestDto dto = CommentDeleteRequestDto.of(1L, 1L);
        Comment domain = Comment.of(dto.getId(), dto.getRecipeId(), null, null, "Y");

        when(commentConverter.deleteRequestDtoToDomain(dto)).thenReturn(domain);
        when(commentUseCase.softDeleteComment(domain)).thenReturn(1L);

        //when & then
        mockMvc.perform(post("/recipe/delete/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @DisplayName("[happy] recipeId에 해당하는 댓글 목록 조회 요청 시 정상적으로 페이징된 데이터와 성공 응답을 반환한다.")
    @Test
    void getCommentListWithValidParams() throws Exception {
        // given
        CommentListResponseDto dto = CommentListResponseDto.of(1L, 1L, "nickname", "commentValue", "2021-01-22", false);
        PagingResponseDto<CommentListResponseDto> pagingResponseDto = PagingResponseDto.of(List.of(dto), 100L);

        when(commentUseCase.getCommentList(anyLong(), anyInt(), anyInt(), eq("new"))).thenReturn(pagingResponseDto);
        //when & then
        mockMvc.perform(get("/recipe/getAllCommentList")
                        .param("recipeId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "new")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalCount").value(pagingResponseDto.getTotalCount()));
    }

    @DisplayName("[happy] 유저가 대댓글 등록 요청 시 정상적으로 저장되고 성공 응답을 반환한다.")
    @Test
    void ifUserCreateSubCommentShouldComplete() throws Exception {
        // given
        SubCommentRegistRequestDto dto = SubCommentRegistRequestDto.of(1L, "subValue");
        SubComment domain = SubComment.of(dto.getParentCommentId(), 1L, dto.getSubCommentText(), "N");
        when(subCommentConverter.registRequestDtoToDomain(dto)).thenReturn(domain);
        when(subCommentUseCase.createSubComment(domain)).thenReturn(1L);

        //when & then
        mockMvc.perform(post("/recipe/regist/subComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andDo(print());
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
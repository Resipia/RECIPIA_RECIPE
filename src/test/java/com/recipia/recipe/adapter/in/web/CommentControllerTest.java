package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.CommentRegistRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.CommentUpdateRequestDto;
import com.recipia.recipe.application.port.in.CommentUseCase;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Comment;
import com.recipia.recipe.domain.converter.CommentConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    // JSON 문자열 변환을 위한 유틸리티 메서드
    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
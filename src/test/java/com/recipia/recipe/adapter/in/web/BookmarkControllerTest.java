package com.recipia.recipe.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.web.dto.request.BookmarkRequestDto;
import com.recipia.recipe.application.port.in.BookmarkUseCase;
import com.recipia.recipe.application.port.in.CreateRecipeUseCase;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Bookmark;
import com.recipia.recipe.domain.converter.BookmarkConverter;
import com.recipia.recipe.domain.converter.RecipeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[통합] 북마크 컨트롤러")
@AutoConfigureMockMvc // @AutoConfigureMockMvc를 사용하여 MockMvc를 이용해 HTTP 요청을 모의로 보낸다.
class BookmarkControllerTest extends TotalTestSupport {

    @Autowired private MockMvc mockMvc;
    @MockBean BookmarkConverter converter;
    @MockBean BookmarkUseCase bookmarkUseCase;
    private ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("북마크 추가 요청 시 정상적으로 처리되고 성공 응답을 반환한다")
    @Test
    void whenAddBookmarkThenSuccess() throws Exception {
        // given
        BookmarkRequestDto bookmarkRequestDto = BookmarkRequestDto.of(1L);
        Bookmark bookmark = Bookmark.of(1L, 1L, 2L);
        when(converter.requestDtoToDomain(any(BookmarkRequestDto.class))).thenReturn(bookmark);
        when(bookmarkUseCase.addBookmark(bookmark)).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/recipe/addBookmark")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookmarkRequestDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("북마크 취소 요청 시 정상적으로 처리되고 성공 응답을 반환한다")
    @Test
    void whenRemoveBookmarkThenSuccess() throws Exception {
        // given
        Long bookmarkId = 1L;

        // when & then
        mockMvc.perform(post("/recipe/removeBookmark")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookmarkId", bookmarkId.toString()))
                .andExpect(status().isOk());
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
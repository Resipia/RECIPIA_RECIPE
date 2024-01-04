package com.recipia.recipe.adapter.in.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipia.recipe.adapter.in.search.constant.SearchType;
import com.recipia.recipe.adapter.in.search.dto.SearchRequestDto;
import com.recipia.recipe.application.port.in.MongoUseCase;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc는 실제 서버를 구동하지 않고 Spring MVC 구조를 모방하여 HTTP 요청과 응답을 테스트한다.
 * 따라서, 실제 데이터베이스나 다른 외부 시스템과의 상호작용은 발생하지 않는다. (오류가 발생 할때마다 이 글을 읽자)
 */
@DisplayName("[통합] 몽고DB 검색 컨트롤러")
@AutoConfigureMockMvc
class MongoSearchControllerTest extends TotalTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MongoUseCase mongoUseCase;


    @DisplayName("[happy] 사용자가 검색어를 입력할때마다 이 컨트롤러가 호출되며 접두사를 기준으로 연관된 데이터를 반환한다.")
    @Test
    void search() throws Exception {
        //given
        SearchRequestDto dto = SearchRequestDto.of(SearchType.INGREDIENTS, "김치", 10);
        List<String> resultList = Arrays.asList("김치", "김치찌개", "김치전");

        when(mongoUseCase.searchWordByPrefix(dto)).thenReturn(resultList);

        //when & then
        mockMvc.perform(get("/mongo/search")
                        .param("condition", dto.getCondition().toString())
                        .param("searchWord", dto.getSearchWord())
                        .param("resultSize", String.valueOf(dto.getResultSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
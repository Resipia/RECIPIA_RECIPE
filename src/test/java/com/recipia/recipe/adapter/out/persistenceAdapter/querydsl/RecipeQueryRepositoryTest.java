package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[통합] 레시피 queryDsl 테스트")
class RecipeQueryRepositoryTest extends TotalTestSupport {

    @Autowired
    private RecipeQueryRepository sut;

    @DisplayName("[happy] 전체 레시피 목록을 페이징하여 조회한다.")
    @Test
    void test() {
        //given
        Long memberId = 1L;
        String sortType = "new";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        //when
        Page<RecipeMainListResponseDto> result = sut.getAllRecipeList(memberId, pageable, sortType);

        //then
        System.out.println(result.getContent().get(0));
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getNumber()).isEqualTo(page);
        assertThat(result.getSize()).isEqualTo(size);
        assertThat(result.getTotalElements()).isGreaterThan(0);
    }

}
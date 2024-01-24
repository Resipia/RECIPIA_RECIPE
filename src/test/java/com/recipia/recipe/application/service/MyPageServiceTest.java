package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.application.port.out.RedisPort;
import com.recipia.recipe.domain.MyPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위] 마이페이지 서비스 테스트")
class MyPageServiceTest {

    @InjectMocks
    private MyPageService sut;
    @Mock
    private RecipePort recipePort;
    @Mock
    private ImageS3Service imageS3Service;
    @Mock
    private RedisPort redisPort;

    @DisplayName("[happy] 유효한 memberId가 들어오면 그 사용자가 작성한 레시피 갯수가 담겨있는 MyPage 도메인을 반환한다.")
    @Test
    void getRecipeCountSeuccess() {
        // given
        Long memberId = 1L;
        Long count = 1L;
        when(recipePort.getMyRecipeCount(memberId)).thenReturn(count);
        MyPage domain = MyPage.of(count);

        // when
        MyPage myPage = sut.getRecipeCount(memberId);
        // then
        assertEquals(domain.getRecipeCount(), myPage.getRecipeCount());
    }

    @DisplayName("[happy] 내가 작성한 레시피 목록중에서 썸네일이 존재하면 preUrl에 데이터가 채워진채로 반환한다.")
    @Test
    void getMyRecipeHighWithPreUrl() {
        // given
        Long memberId = 1L;
        List<Long> myRecipeIds = List.of(1L, 2L);
        String preSignedUrl = "https://example.com/s3/pre-signed-url";
        RecipeMainListResponseDto dtoWithPreSignedUrl = RecipeMainListResponseDto.of(1L, "레시피명", "닉네임", 1L, null, null, preSignedUrl);

        when(recipePort.getAllMyRecipeIds(memberId)).thenReturn(myRecipeIds);
        when(recipePort.getMyHighRecipeList(eq(memberId), anyList())).thenReturn(List.of(dtoWithPreSignedUrl));

        // when
        List<RecipeMainListResponseDto> result = sut.getMyRecipeHigh(memberId);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getThumbnailPreUrl());
    }


    @DisplayName("[happy] 내가 작성한 레시피 목록중에서 썸네일이 존재하지 않으면 preUrl에 데이터가 없이 반환한다.")
    @Test
    void getMyRecipeHighWithoutPreUrl() {
        // given
        Long memberId = 1L;
        List<Long> myRecipeIds = List.of(1L, 2L);
        String preSignedUrl = "https://example.com/s3/pre-signed-url";
        RecipeMainListResponseDto dtoWithPreSignedUrl = RecipeMainListResponseDto.of(1L, "레시피명", "닉네임", 1L, null, null, preSignedUrl);
        RecipeMainListResponseDto dtoWithoutPreSignedUrl = RecipeMainListResponseDto.of(1L, "레시피명", "닉네임", 1L, null, null, null);
        List<RecipeMainListResponseDto> finalResult = List.of(dtoWithPreSignedUrl, dtoWithoutPreSignedUrl);

        when(recipePort.getAllMyRecipeIds(memberId)).thenReturn(myRecipeIds);
        when(recipePort.getMyHighRecipeList(eq(memberId), anyList())).thenReturn(finalResult);

        // when
        List<RecipeMainListResponseDto> result = sut.getMyRecipeHigh(memberId);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertNull(result.get(1).getThumbnailPreUrl());
    }


}
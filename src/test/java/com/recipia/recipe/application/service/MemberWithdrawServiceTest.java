package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.out.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위] 회원 탈퇴 관련 Service 테스트")
class MemberWithdrawServiceTest {

    @InjectMocks MemberWithdrawService sut;
    @Mock private RecipePort recipePort;
    @Mock private CommentPort commentPort;
    @Mock private BookmarkPort bookmarkPort;
    @Mock private RecipeLikePort recipeLikePort;
    @Mock private NicknamePort nicknamePort;

    @DisplayName("[happy] memberId가 작성한 레시피가 존재할때 레시피 관련 데이터 삭제 메서드를 호출한다.")
    @Test
    void memberWriteRecipes() {
        // given
        Long memberId = 1L;
        List<Long> recipeIds = List.of(1L, 2L);

        when(recipePort.getAllRecipeIdsByMemberId(memberId)).thenReturn(recipeIds);
        // when
        sut.deleteRecipeByMemberId(memberId);
        // then
        verify(recipePort).getAllRecipeIdsByMemberId(memberId);
        verify(recipePort).softDeleteRecipeByMemberId(memberId);
        verify(recipePort).softDeleteRecipeFilesInRecipeIds(recipeIds);
        verify(recipePort).deleteNutritionalInfosInRecipeIds(recipeIds);
        verify(recipePort).deleteRecipeCategoryMapsInRecipeIds(recipeIds);
        verify(commentPort).softDeleteCommentsAndSubCommentsInRecipeIds(recipeIds);
    }

    @DisplayName("[happy] memberId가 작성한 레시피가 존재하지 않을때 레시피 관련 데이터 삭제 메서드가 호출되지 않는다.")
    @Test
    void memberNotWriteRecipes() {
        // given
        Long memberId = 3L;
        List<Long> recipeIds = List.of();

        when(recipePort.getAllRecipeIdsByMemberId(memberId)).thenReturn(recipeIds);
        // when
        sut.deleteRecipeByMemberId(memberId);
        // then
        verify(recipePort).getAllRecipeIdsByMemberId(memberId);

        verify(recipePort, never()).softDeleteRecipeByMemberId(memberId);
        verify(recipePort, never()).softDeleteRecipeFilesInRecipeIds(recipeIds);
        verify(recipePort, never()).deleteNutritionalInfosInRecipeIds(recipeIds);
        verify(recipePort, never()).deleteRecipeCategoryMapsInRecipeIds(recipeIds);
        verify(commentPort, never()).softDeleteCommentsAndSubCommentsInRecipeIds(recipeIds);
    }
}
package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.MemberWithdrawUseCase;
import com.recipia.recipe.application.port.out.BookmarkPort;
import com.recipia.recipe.application.port.out.CommentPort;
import com.recipia.recipe.application.port.out.RecipeLikePort;
import com.recipia.recipe.application.port.out.RecipePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 멤버 탈퇴 관련 서비스 클래스
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberWithdrawService implements MemberWithdrawUseCase {

    private final RecipePort recipePort;
    private final CommentPort commentPort;
    private final BookmarkPort bookmarkPort;
    private final RecipeLikePort recipeLikePort;

    @Override
    public Long deleteRecipeByMemberId(Long memberId) {

        // memberId가 작성한 레시피 id를 목록으로 가져온다.
        List<Long> recipeIds = recipePort.getAllRecipeIdsByMemberId(memberId);

        // memberId에 해당하는 레시피를 전부 삭제한다.
        recipePort.softDeleteRecipeByMemberId(memberId);

        // recipeId에 해당하는 레시피 파일을 전부 삭제한다.
        recipePort.softDeleteRecipeFilesInRecipeIds(recipeIds);

        // 레시피 영양소 정보를 전부 삭제한다.
        recipePort.deleteNutritionalInfosInRecipeIds(recipeIds);

        // 사용자가 작성한 레시피에 해당하는 카테고리 맵핑을 전부 삭제한다.
        recipePort.deleteRecipeCategoryMapsInRecipeIds(recipeIds);

        // memberId에 해당하는 북마크를 전부 삭제한다.
        bookmarkPort.deleteBookmarkByMemberId(memberId);

        // 회원이 한 좋아요를 전부 삭제한다.
        recipeLikePort.deleteLikeByMemberId(memberId);

        // 내가 작성한 레시피에 달린 댓글/대댓글을 전부 삭제한다.
        commentPort.softDeleteCommentsAndSubCommentsInRecipeIds(recipeIds);

        // 회원이 작성한 댓글/대댓글을 전부 삭제한다.
        commentPort.softDeleteCommentsAndSubCommentsInMemberId(memberId);

        return 0L;
    }
}

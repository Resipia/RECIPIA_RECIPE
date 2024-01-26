package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.MemberWithdrawUseCase;
import com.recipia.recipe.application.port.out.*;
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
    private final NicknamePort nicknamePort;

    /**
     * [DELETE] memberId에 해당하는 레시피 관련 모든 데이터 전부 삭제
     */
    @Transactional
    @Override
    public Long deleteRecipeByMemberId(Long memberId) {

        // memberId가 작성한 레시피 id를 목록으로 가져온다.
        List<Long> recipeIds = recipePort.getAllRecipeIdsByMemberId(memberId);

        // 회원이 작성한 레시피가 있을때만 아래 프로세스 진행.
        if (!recipeIds.isEmpty()) {
            // memberId에 해당하는 레시피를 전부 삭제한다.
            recipePort.softDeleteRecipeByMemberId(memberId);

            // recipeId에 해당하는 레시피 파일을 전부 삭제한다.
            recipePort.softDeleteRecipeFilesInRecipeIds(recipeIds);

            // recipeId에 해당하는 북마크를 전부 삭제한다.
            bookmarkPort.deleteBookmarksInRecipeIds(recipeIds);

            // recipeId에 해당하는 좋아요를 전부 삭제한다.
            recipeLikePort.deleteRecipeLikesInRecipeIds(recipeIds);

            // 레시피 영양소 정보를 전부 삭제한다.
            recipePort.deleteNutritionalInfosInRecipeIds(recipeIds);

            // 사용자가 작성한 레시피에 해당하는 카테고리 맵핑을 전부 삭제한다.
            recipePort.deleteRecipeCategoryMapsInRecipeIds(recipeIds);

            // 내가 작성한 레시피에 달린 댓글/대댓글을 전부 삭제한다.
            commentPort.softDeleteCommentsAndSubCommentsInRecipeIds(recipeIds);

        }

        // memberId에 해당하는 북마크를 전부 삭제한다.
        bookmarkPort.deleteBookmarkByMemberId(memberId);

        // 회원이 한 좋아요를 전부 삭제한다.
        recipeLikePort.deleteLikeByMemberId(memberId);

        // 회원이 작성한 댓글/대댓글을 전부 삭제한다.
        commentPort.softDeleteCommentsAndSubCommentsInMemberId(memberId);

        // 회원의 닉네임을 삭제한다.
        nicknamePort.deleteNickname(memberId);

        // todo: redis에서 삭제된 레시피 조회수 삭제
        // todo: 나중에 batch로 S3에서 삭제처리된 파일들 일괄삭제


        return 0L;
    }
}

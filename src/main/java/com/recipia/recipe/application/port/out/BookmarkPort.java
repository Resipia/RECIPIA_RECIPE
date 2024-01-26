package com.recipia.recipe.application.port.out;

import com.recipia.recipe.domain.Bookmark;

import java.util.List;

public interface BookmarkPort {

    Long addBookmark(Bookmark bookmark);

    void removeBookmark(Long bookmarkId);
    Long deleteBookmarksInRecipeIds(List<Long> recipeIds);

    Long deleteBookmarkByMemberId(Long memberId);
}

package com.recipia.recipe.application.port.out;

import com.recipia.recipe.domain.Bookmark;

public interface BookmarkPort {

    Long addBookmark(Bookmark bookmark);

    void removeBookmark(Long bookmarkId);
    Long deleteBookmarkByRecipeId(Long recipeId);

    Long deleteBookmarkByMemberId(Long memberId);
}

package com.recipia.recipe.application.port.in;

import com.recipia.recipe.domain.Bookmark;

public interface BookmarkUseCase {

    Long addBookmark(Bookmark bookmark);

    void removeBookmark(Long bookmarkId);
}

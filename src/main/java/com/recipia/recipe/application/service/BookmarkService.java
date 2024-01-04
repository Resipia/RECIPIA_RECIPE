package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.BookmarkUseCase;
import com.recipia.recipe.application.port.out.BookmarkPort;
import com.recipia.recipe.domain.Bookmark;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkService implements BookmarkUseCase {

    private final BookmarkPort bookmarkPort;

    /**
     * 북마크 추가하는 메서드
     */
    @Transactional
    @Override
    public Long addBookmark(Bookmark bookmark) {
        return bookmarkPort.addBookmark(bookmark);
    }

    @Transactional
    public void removeBookmark(Long bookmarkId) {
        bookmarkPort.removeBookmark(bookmarkId);
    }

}

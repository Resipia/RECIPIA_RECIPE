package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.out.BookmarkPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.Bookmark;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위] 북마크 서비스 테스트")
class BookmarkServiceTest{

    @InjectMocks
    BookmarkService sut;

    @Mock
    BookmarkPort bookmarkPort;

    @DisplayName("[happy] 북마크 등록을 시도하면 BookmarkPort의 bookmarkPort를 호출한다.")
    @Test
    void addBookmark() {
        //given
        Bookmark bookmark = Bookmark.of(1L, 1L);

        //when
        sut.addBookmark(bookmark);

        //then
        verify(bookmarkPort).addBookmark(bookmark);
    }

    @DisplayName("[bad] 북마크를 등록할때 레시피 정보가 없으면 예외가 발생한다.")
    @Test
    void addBookmarkException1() {
        //given
        Bookmark bookmark = Bookmark.of(null, 1L);
        Mockito.when(bookmarkPort.addBookmark(any(Bookmark.class)))
                .thenThrow(new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND));

        //when & then
        Assertions.assertThatThrownBy(() -> sut.addBookmark(bookmark))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }


    @DisplayName("[bad] 북마크를 등록할때 멤버 정보가 없으면 예외가 발생한다.")
    @Test
    void addBookmarkException2() {
        //given
        Bookmark bookmark = Bookmark.of(1L, null);
        Mockito.when(bookmarkPort.addBookmark(any(Bookmark.class)))
                .thenThrow(new RecipeApplicationException(ErrorCode.USER_NOT_FOUND));

        //when & then
        Assertions.assertThatThrownBy(() -> sut.addBookmark(bookmark))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("유저를 찾을 수 없습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("[bad] 북마크 추가 중 데이터베이스 오류가 발생하면 예외가 발생한다.")
    @Test
    void addBookmarkDatabaseError() {
        //given
        Bookmark bookmark = Bookmark.of(1L, 1L);
        Mockito.when(bookmarkPort.addBookmark(any(Bookmark.class)))
                .thenThrow(new DataAccessException("데이터베이스 오류 발생") {});

        //when & then
        Assertions.assertThatThrownBy(() -> sut.addBookmark(bookmark))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("데이터베이스 오류 발생");
    }

    @DisplayName("[bad] 북마크 추가 시 발생하는 예외의 세부 정보를 확인한다.")
    @Test
    void addBookmarkExceptionDetails() {
        //given
        Bookmark bookmark = Bookmark.of(1L, 1L);
        Mockito.when(bookmarkPort.addBookmark(any(Bookmark.class)))
                .thenThrow(new RecipeApplicationException(ErrorCode.INVALID_INPUT));

        //when & then
        Assertions.assertThatThrownBy(() -> sut.addBookmark(bookmark))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("잘못된 입력입니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
    }

    @DisplayName("[happy] 존재하는 북마크를 제거하면 BookmarkPort의 removeBookmark를 호출한다.")
    @Test
    void removeBookmark() {
        //given
        Long bookmarkId = 1L;

        //when
        sut.removeBookmark(bookmarkId);

        //then
        verify(bookmarkPort).removeBookmark(bookmarkId);
    }

    @DisplayName("[bad] 존재하지 않는 북마크를 제거하려고 하면 예외가 발생한다.")
    @Test
    void removeBookmarkException() {
        //given
        Long nonExistentBookmarkId = 100L;
        Mockito.doThrow(new RecipeApplicationException(ErrorCode.BOOKMARK_NOT_FOUND))
                .when(bookmarkPort).removeBookmark(nonExistentBookmarkId);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.removeBookmark(nonExistentBookmarkId))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("북마크를 찾을 수 없습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOOKMARK_NOT_FOUND);
    }

    @DisplayName("[bad] 북마크 제거 중 데이터베이스 오류가 발생하면 예외가 발생한다.")
    @Test
    void removeBookmarkDatabaseError() {
        //given
        Long bookmarkId = 1L;
        Mockito.doThrow(new DataAccessException("데이터베이스 오류 발생") {})
                .when(bookmarkPort).removeBookmark(bookmarkId);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.removeBookmark(bookmarkId))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("데이터베이스 오류 발생");
    }


}
package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.BookmarkEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.config.TotalTestSupport;
import com.recipia.recipe.domain.Bookmark;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[통합] 북마크 Adapter 테스트")
class BookmarkAdapterTest extends TotalTestSupport {

    @Autowired
    private BookmarkAdapter sut;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private RecipeRepository recipeRepository;


    @DisplayName("[happy] 북마크 추가에 성공하면 id값을 반환한다.")
    @Test
    void addBookmark() {
        //given
        RecipeEntity recipeEntity = createRecipeEntity();
        RecipeEntity savedRecipe = recipeRepository.save(recipeEntity);

        Bookmark bookmark = Bookmark.of(savedRecipe.getId(), 1L);

        //when
        Long addedBookmark = sut.addBookmark(bookmark);

        //then
        BookmarkEntity savedBookmarkEntity = bookmarkRepository.findById(addedBookmark).get();
        Assertions.assertThat(addedBookmark).isNotNull();
        Assertions.assertThat(addedBookmark).isEqualTo(savedBookmarkEntity.getId());
    }

    @DisplayName("[bad] 존재하지 않는 레시피를 북마크 추가하면 예외가 발생한다.")
    @Test
    void addBookmarkException() {
        //given
        Bookmark bookmark = Bookmark.of(100L, 1L);

        //when & then
        Assertions.assertThatThrownBy(() -> sut.addBookmark(bookmark))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("레시피가 존재하지 않습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECIPE_NOT_FOUND);
    }

    @DisplayName("[happy] 존재하는 북마크를 제거하면 성공한다.")
    @Test
    void removeBookmarkSuccess() {
        //given
        RecipeEntity recipeEntity = createRecipeEntity();
        RecipeEntity savedRecipe = recipeRepository.save(recipeEntity);
        BookmarkEntity bookmarkEntity = BookmarkEntity.of(savedRecipe, 1L);
        BookmarkEntity savedBookmark = bookmarkRepository.save(bookmarkEntity);

        //when
        sut.removeBookmark(savedBookmark.getId());

        //then
        Assertions.assertThat(bookmarkRepository.existsById(savedBookmark.getId())).isFalse();
    }

    @DisplayName("[bad] 존재하지 않는 북마크를 제거하려고 하면 예외가 발생한다.")
    @Test
    void removeBookmarkException() {
        //given
        Long nonExistentBookmarkId = 100L;

        //when & then
        Assertions.assertThatThrownBy(() -> sut.removeBookmark(nonExistentBookmarkId))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("북마크를 찾을 수 없습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOOKMARK_NOT_FOUND);
    }

    private RecipeEntity createRecipeEntity() {
        return RecipeEntity.of(
                1L,
                "레시피",
                "레시피 설명",
                20,
                "김치, 감자",
                "#고구마",
                "진안",
                0,
                "N"
        );
    }


}
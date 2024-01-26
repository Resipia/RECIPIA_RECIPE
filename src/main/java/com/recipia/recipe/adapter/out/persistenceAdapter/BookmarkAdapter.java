package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.BookmarkEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.BookmarkQuerydslRepository;
import com.recipia.recipe.application.port.out.BookmarkPort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.domain.Bookmark;
import com.recipia.recipe.domain.converter.BookmarkConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BookmarkAdapter implements BookmarkPort {

    private final BookmarkConverter converter;
    private final BookmarkRepository bookmarkRepository;
    private final RecipeRepository recipeRepository;
    private final BookmarkQuerydslRepository bookmarkQuerydslRepository;

    /**
     * 북마크 추가하는 메서드
     * 추가에 성공하면 추가한 북마크 pk(id) 값을 반환한다.
     */
    @Override
    public Long addBookmark(Bookmark bookmark) {

        // 레시피 존재 여부 예외처리
        recipeRepository.findByIdAndDelYn(bookmark.getRecipeId(), "N").orElseThrow(
                () -> new RecipeApplicationException(ErrorCode.RECIPE_NOT_FOUND)
        );

        // 컨버터 내부에서 멤버 id 예외처리 실시
        BookmarkEntity bookmarkEntity = converter.domainToEntity(bookmark);
        return bookmarkRepository.save(bookmarkEntity).getId();
    }

    @Override
    public void removeBookmark(Long bookmarkId) {

        // 북마크 존재여부 확인
        BookmarkEntity bookmarkEntity = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RecipeApplicationException(ErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmarkEntity);
    }

    /**
     * [DELETE] 레시피 id로 북마크 삭제
     */
    @Override
    public Long deleteBookmarkByRecipeId(Long recipeId) {
        return bookmarkQuerydslRepository.deleteBookmarkByRecipeId(recipeId);
    }

    /**
     * [DELETE] memberId에 해당하는 북마크 삭제
     */
    @Override
    public Long deleteBookmarkByMemberId(Long memberId) {
        return bookmarkQuerydslRepository.deleteBookmarkByMemberId(memberId);
    }

}

package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.out.persistence.entity.BookmarkEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.BookmarkRepository;
import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DisplayName("[통합] 북마크 querydsl 테스트")
class BookmarkQuerydslRepositoryTest extends TotalTestSupport {

    @Autowired
    private BookmarkQuerydslRepository sut;
    @Autowired
    private BookmarkRepository bookmarkRepository;

    @DisplayName("[happy] recipeId에 해당하는 북마크를 삭제한다.")
    @Test
    void deleteBookmarkByRecipeId() {
        // given
        List<Long> recipeIds = List.of(1L);
        // when
        Long deletedCount = sut.deleteBookmarksInRecipeIds(recipeIds);
        // then
        List<BookmarkEntity> allByRecipeEntityId = bookmarkRepository.findAllByRecipeEntity_Id(recipeIds.get(0));
        assertThat(allByRecipeEntityId.size()).isEqualTo(0);
    }

    @DisplayName("[happy] memberId에 해당하는 북마크를 삭제한다.")
    @Test
    void deleteBookmarkByMemberId() {
        // given
        Long memberId = 1L;
        // when
        Long deletedCount = sut.deleteBookmarkByMemberId(memberId);
        // then
        List<BookmarkEntity> allByMemberId = bookmarkRepository.findAllByMemberId(memberId);
        assertThat(allByMemberId.size()).isEqualTo(0);
    }


}
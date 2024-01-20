package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.BookmarkRequestDto;
import com.recipia.recipe.adapter.out.persistence.entity.BookmarkEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.Bookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookmarkConverter {

    private final SecurityUtil securityUtil;

    /**
     * dto to domain
     * 서비스 레이어에 도메인을 보내기 위해서 dto 객체를 도메인으로 변환하는 메서드
     */
    public Bookmark requestDtoToDomain(BookmarkRequestDto bookmarkRequestDto) {
        return Bookmark.of(
                bookmarkRequestDto.getRecipeId(),
                securityUtil.getCurrentMemberId()
        );
    }

    /**
     *
     * domain to entity
     * 최적화를 위해 레시피 엔티티는 레시피 id만 추가한다. (pk만 있으면 save 가능)
     */
    public BookmarkEntity domainToEntity(Bookmark bookmark) {
        return BookmarkEntity.of(
                RecipeEntity.of(bookmark.getRecipeId()),
                bookmark.getMemberId()
        );
    }
}

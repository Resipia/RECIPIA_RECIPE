package com.recipia.recipe.adapter.in.web.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 북마크 등록 dto
 */
@Data
@NoArgsConstructor
public class BookmarkDto {

    private Long recipeId;
    private Long memberId;

    @Builder
    private BookmarkDto(Long recipeId, Long memberId) {
        this.recipeId = recipeId;
        this.memberId = memberId;
    }

    public static BookmarkDto of(Long recipeId, Long memberId) {
        return new BookmarkDto(recipeId, memberId);
    }

}

package com.recipia.recipe.adapter.in.web.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 북마크 등록 dto
 */
@Data
@NoArgsConstructor
public class BookmarkRequestDto {

    private Long recipeId;

    @Builder
    private BookmarkRequestDto(Long recipeId) {
        this.recipeId = recipeId;
    }

    public static BookmarkRequestDto of(Long recipeId) {
        return new BookmarkRequestDto(recipeId);
    }

}

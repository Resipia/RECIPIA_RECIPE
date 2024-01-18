package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 메인 페이지의 레시피 조회를 할때 데이터를 전달할 객체
 */
@NoArgsConstructor
@Data
public class RecipeMainListResponseDto {
    private Long id;
    private String recipeName;
    private String nickname;
    private List<String> subCategoryList;
    private Long bookmarkId;
    // todo: 추후 썸네일 이미지 추가
    //제목, 닉넴, 카테고리(list or string), 북마크 여부, 조회수, order by (조회수 높고낮음, 등록날짜 높고낮음)

    public RecipeMainListResponseDto(Long id, String recipeName, String nickname, Long bookmarkId) {
        this.id = id;
        this.recipeName = recipeName;
        this.nickname = nickname;
        this.bookmarkId = bookmarkId;
    }

    public static RecipeMainListResponseDto of(Long id, String recipeName, String nickname, Long bookmarkId) {
        return new RecipeMainListResponseDto(id, recipeName, nickname, bookmarkId);
    }

    public static RecipeMainListResponseDto of(String recipeName, String nickname, Long bookmarkId) {
        return new RecipeMainListResponseDto(null, recipeName, nickname, bookmarkId);
    }

}
